from optparse import Values
import streamlit as st
import subprocess
import os
import polars as pl
from utils import xlsx_to_csv, csv_to_xlsx, load_config, update_config, CONFIG_DISPLAY_NAMES, LIST_CONFIGS

st.set_page_config(page_title="All Team Hat", layout="wide")
st.title("All Team Hat 🚀")


# Trois colonnes pour l'interface
col1, col2, col3, col4 = st.columns(4)

uploaded_file = st.file_uploader("Upload du fichier CSV", type=["xlsx", "xls"])

config_name = st.selectbox(
    "Choisir un profil de configuration :",
    options=CONFIG_DISPLAY_NAMES,
    key='config_selector',
    on_change=update_config # Fonction appelée lors du changement
)

config = load_config(config_name)

with st.container():
    with col1:
        nbTeams = col1.number_input("nbTeams", min_value=2, value=config.get("nb_teams"))
        first_name_col = col1.text_input("Nom de la colonne Prénom", value=config.get("first_name_col"))
        club_col = col1.text_input("Nom de la colonne Club", value=config.get("club_col"))
        handling_col = col1.text_input("Nom de la colonne Handling", value=config.get("handling_col"))

    with col2:
        nbRuns = col2.number_input("nbRuns", min_value=2, max_value=20, value=config.get("nb_runs"))
        last_name_col = col2.text_input("Nom de la colonne Nom", value=config.get("last_name_col"))
        age_col = col2.text_input("Nom de la colonne Age", value=config.get("age_col"))
        handler = col2.text_input("Valeur pour handler", value=config.get("handler"))

    with col3:
        number_of_skills = col3.number_input("Nombre de compétences", min_value=1, max_value=10, value=config.get("number_of_skills"))
        nickname_col = col3.text_input("Nom de la colonne Surnom", value=config.get("nickname_col"))
        gender_col = col3.text_input("Nom de la colonne Genre", value=config.get("gender_col"))
        middle = col3.text_input("Valeur pour middle", value=config.get("middle"))

    with col4:
        first_skill_col = col4.number_input("N° de la colonne de la 1ère compétence", min_value=1, value=config.get("first_skill_col"))
        email_col = col4.text_input("Nom de la colonne Email", value=config.get("email_col"))

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

st.divider()
col_feedback, col_donate = st.columns(2)

with col_feedback:
    st.markdown("[📝 N'hésitez pas à faire des suggestions d'amélioration ici](https://github.com/julien-gm/all-team-hat/issues)")

with col_donate:
    st.markdown("[💝 Le projet vous a plu, n'hésitez pas à soutenir cette initiative 💝](https://paypal.me/gmjulien)")
