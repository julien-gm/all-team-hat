import json
import polars as pl
import streamlit as st


LIST_CONFIGS = {
    "Configuration par défaut": "default", 
    "Carap'hat": "caraphat",
    "Diagonale du disc": "hat_nevers",
    "Symp'hat": "symphat"
}
CONFIG_DISPLAY_NAMES = list(LIST_CONFIGS.keys())


def update_config():
    """Charge et stocke la nouvelle configuration sélectionnée dans l'état de session."""
    selected_name = st.session_state.config_selector
    
    # Charger la nouvelle configuration
    new_config = load_config(selected_name)
    
    if new_config is not None:
        # Stocker la nouvelle configuration dans l'état de session pour les widgets
        return new_config
        
        # Le code Streamlit sera ré-exécuté, mais les widgets utiliseront ces nouvelles valeurs.
    raise Exception("Échec du chargement. Veuillez sélectionner une autre configuration.")


def load_config(config_key):
    """Charge la configuration depuis le fichier JSON correspondant à la clé."""
    filename = LIST_CONFIGS.get(config_key)
    if not filename:
        raise Exception(f"Clé de configuration inconnue : {config_key}")
        
    file_path = f"src/config_{filename}.json"
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    except FileNotFoundError:
        raise Exception(f"⚠️ Fichier de configuration non trouvé : {file_path}")
    except json.JSONDecodeError:
        raise Exception(f"Erreur de format JSON dans le fichier : {file_path}")

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
