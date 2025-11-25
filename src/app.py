import streamlit as st
import subprocess
import os

st.title("All Team Hat 🚀")

uploaded_file = st.file_uploader("Upload du fichier CSV", type=["csv"])
nbTeams = st.number_input("nbTeams", min_value=1, value=2)
nbRuns = st.number_input("nbRuns", min_value=1, value=2)

if uploaded_file and st.button("Lancer l'application"):
    # Sauvegarde du fichier uploadé
    input_path = "input.csv"
    with open(input_path, "wb") as f:
        f.write(uploaded_file.getbuffer())

    jar_name = "all-team-hat.jar"
    cmd = [
        "java", "-cp", jar_name, "CalculatorJob",
        "-nbTeams", str(nbTeams),
        "-nbRuns", str(nbRuns),
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
            output_container.text('\n'.join(lines))
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