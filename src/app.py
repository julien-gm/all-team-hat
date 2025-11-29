import streamlit as st
import subprocess
import os
import polars as pl


def xlsx_to_csv(entree, sortie):
    df = pl.read_excel(entree)
    df.write_csv(sortie, separator=',')


def csv_to_xlsx(entree, sortie):
    df = pl.read_csv(
        entree,
        separator=',',
        truncate_ragged_lines=True,
        infer_schema_length=1000,
        ignore_errors=True
    )
    df.write_excel(sortie)

st.set_page_config(page_title="All Team Hat", layout="wide")
st.title("All Team Hat 🚀")

# Trois colonnes pour l'interface
col1, col2, col3, col4 = st.columns(4)

uploaded_file = st.file_uploader("Upload du fichier CSV", type=["xlsx", "xls"])

with col1:
    nbTeams = col1.number_input("nbTeams", min_value=2, value=4)
    first_name_col = col1.text_input("Nom de la colonne Prénom", value="Prénom")
    club_col = col1.text_input("Nom de la colonne Club", value="Club")
    handling_col = col1.text_input("Nom de la colonne Handling", value="Handler?")

with col2:
    nbRuns = col2.number_input("nbRuns", min_value=2, max_value=20, value=5)
    last_name_col = col2.text_input("Nom de la colonne Nom", value="Nom")
    age_col = col2.text_input("Nom de la colonne Age", value="Age")
    handler = col2.text_input("Valeur pour handler", value="Oui")

with col3:
    number_of_skills = col3.number_input("Nombre de compétences", min_value=1, max_value=10, value=3)
    nickname_col = col3.text_input("Nom de la colonne Surnom", value="Pseudo")
    gender_col = col3.text_input("Nom de la colonne Genre", value="Genre")
    middle = col3.text_input("Valeur pour middle", value="Non")

with col4:
    first_skill_col = col4.number_input("N° de la colonne de la 1ère compétence", min_value=1, value=8)
    email_col = col4.text_input("Nom de la colonne Email", value="Email")

if uploaded_file and st.button("Lancer l'application"):
    # Sauvegarde du fichier uploadé
    ext = os.path.splitext(uploaded_file.name)[-1].lower()
    input_path = f"input.{ext}"
    input_csv = "input.csv"
    with open(input_path, "wb") as f:
        f.write(uploaded_file.getbuffer())
        xlsx_to_csv(input_path, input_csv)

    jar_name = "target/all-team-hat-2.2.0-jar-with-dependencies.jar"
    cmd = [
        "java", "-cp", jar_name, "CalculatorJob",
        "-nbTeams", str(nbTeams),
        "-nbRuns", str(nbRuns),
        "-firstNameCol", first_name_col,
        "-lastNameCol", last_name_col,
        "-clubCol", club_col,
        "-ageCol", age_col,
        "-emailCol", email_col,
        "-genderCol", gender_col,
        "-nicknameCol", nickname_col,
        "-handlingCol", handling_col,
        "-handlerValue", handler,
        "-middleValue", middle,
        "-nbSkills", str(number_of_skills),
        "-skillFirstCol", str(first_skill_col-1),
        "-file", input_csv
    ]

    # Lancement du process et lecture en temps réel (stdout et stderr combinés)
    process = subprocess.Popen(
        cmd,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        bufsize=1,
        universal_newlines=True
    )

    try:
        with st.container(height=200):
            with st.chat_message("logs"):
                st.markdown("⏳ Exécution en cours...")
                for raw_line in iter(process.stdout.readline, ''):
                    if raw_line == '' and process.poll() is not None:
                        break
                    # Ajoute la ligne reçue et met à jour l'affichage
                    st.markdown(raw_line.rstrip('\n'))
        process.wait()
    except Exception as e:
        process.kill()
        st.error(f"Erreur lors de l'exécution : {e}")
    finally:
        if process.stdout:
            process.stdout.close()

    if process.returncode and process.returncode != 0:
        st.error(f"Process terminé avec le code {process.returncode}")
    else:
        st.success("Terminé ✅")

    output_file = "last_run.csv"
    if os.path.exists(output_file):
        output_xlsx = "teams.xlsx"
        csv_to_xlsx(output_file, output_xlsx)
        with open(output_xlsx, "rb") as f:
            st.download_button(
                label="Télécharger le résultat",
                data=f,
                file_name=output_xlsx,
                mime="application/vnd.ms-excel"
            )
