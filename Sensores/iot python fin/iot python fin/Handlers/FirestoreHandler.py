import firebase_admin
from firebase_admin import credentials, firestore
from threading import Lock
from Handlers.Registro import Registro


class FirestoreHandler:
    _instance = None
    _lock = Lock()

    CREDENTIALS_PATH = "config/petcare-firebase-adminsdk.json"
    DATABASE_URL = "https://petcare-3e54a.firebaseio.com"

    def __init__(self):
        if FirestoreHandler._instance is not None:
            raise Exception("Esta clase es un singleton. Usa get_instance() para obtener la instancia.")

        try:
            # Inicializar las credenciales desde el archivo JSON.
            cred = credentials.Certificate(FirestoreHandler.CREDENTIALS_PATH)
            firebase_admin.initialize_app(cred, {
                'databaseURL': FirestoreHandler.DATABASE_URL
            })

            self.firestore = firestore.client()
            print("Firestore inicializado correctamente.")
        except Exception as e:
            print(f"Error al inicializar Firestore: {e}")

    @classmethod
    def get_instance(cls):
        if cls._instance is None:
            with cls._lock:
                if cls._instance is None:
                    cls._instance = FirestoreHandler()
        return cls._instance

    def publish_message(self, user_id, hora, registro: Registro):
        try:
            # Crear referencia al documento: users/{userId}/hora/{hora}
            doc_ref = self.firestore.collection("usersregistros").document(user_id).collection("hora").document(hora)

            # Crear un mapa para los datos.
            data_map = {
                "hora": hora,
                "registro": registro.to_dict()  # Convierte el objeto Registro a un diccionario.
            }

            # Publicar los datos en Firestore.
            doc_ref.set(data_map)
            print(f"Registro publicado en Firestore para el usuario {user_id} a la hora {hora}.")
        except Exception as e:
            print(f"Error al publicar el registro en Firestore: {e}")
