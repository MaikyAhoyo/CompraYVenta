package com.example.appcomprayventa.modelos

class ModeloComentario {
    var idComentario: String = ""
    var uid: String = ""
    var comentario: String = ""
    var timestamp: String = ""

    constructor()

    constructor(idComentario: String, uid: String, comentario: String, timestamp: String) {
        this.idComentario = idComentario
        this.uid = uid
        this.comentario = comentario
        this.timestamp = timestamp
    }
}