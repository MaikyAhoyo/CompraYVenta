package com.example.appcomprayventa.modelos

class ModeloRespuesta {
    var idRespuesta: String = ""
    var idComentario: String = ""
    var uid: String = ""
    var respuesta: String = ""
    var timestamp: String = ""

    constructor()

    constructor(idRespuesta: String, idComentario: String, uid: String, respuesta: String, timestamp: String) {
        this.idRespuesta = idRespuesta
        this.idComentario = idComentario
        this.uid = uid
        this.respuesta = respuesta
        this.timestamp = timestamp
    }
}