import streamlit as st
import subprocess
import os

st.set_page_config(page_title="All Team Hat", layout="wide")
st.title("All Team Hat 🚀")

# Trois colonnes pour l'interface
col1, col2, col3, col4 = st.columns(4)

uploaded_file = st.file_uploader("Upload du fichier CSV", type=["csv"])
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
    first_skill_col = col4.number_input("Index de la première colonne de compétence (en commençant à 0)", min_value=0, value=8)
    email_col = col4.text_input("Nom de la colonne Email", value="Email")

if uploaded_file and st.button("Lancer l'application"):
    # Sauvegarde du fichier uploadé
    input_path = "input.csv"
    with open(input_path, "wb") as f:
        f.write(uploaded_file.getbuffer())

    jar_name = "/home/ubuntu/myapp/all-team-hat-2.2.0-jar-with-dependencies.jar"
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
        "-skillFirstCol", str(first_skill_col),
        "-file", input_path
    ]

    st.write("⏳ Exécution en cours...")

    output_container = st.empty()
    lines = []

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
        for raw_line in iter(process.stdout.readline, ''):
            if raw_line == '' and process.poll() is not None:
                break
            # Ajoute la ligne reçue et met à jour l'affichage
            lines.append(raw_line.rstrip('\n'))
            output_container.text_area("Logs", '\n'.join(lines), height=400)
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

    # Récupération du résultat final
    # result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

    # st.text(result.stdout)
    # if result.stderr:
    #     st.error(result.stderr)

    output_file = "last_run.csv"
    if os.path.exists(output_file):
        with open(output_file, "rb") as f:
            st.download_button(
                label="Télécharger le résultat",
                data=f,
                file_name=output_file,
                mime="text/csv"
            )