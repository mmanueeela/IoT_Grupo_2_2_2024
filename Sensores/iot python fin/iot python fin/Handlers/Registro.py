class Registro:
    def __init__(self, movimiento: bool, bolla: bool, distancia: bool, uid: str, percent: float):
        self.movimiento = movimiento
        self.bolla = bolla
        self.distancia = distancia
        self.uid = uid
        self.percent = percent

    def to_dict(self):
        return {
            "movimiento": self.movimiento,
            "bolla": self.bolla,
            "distancia": self.distancia,
            "uid": self.uid,
            "percent": self.percent
        }

    def __str__(self):
        return (
            f"Registro(movimiento={self.movimiento}, bolla={self.bolla}, "
            f"distancia={self.distancia}, uid='{self.uid}', percent={self.percent})"
        )

