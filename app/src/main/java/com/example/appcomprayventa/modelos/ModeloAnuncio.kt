package com.example.appcomprayventa.modelos

class ModeloAnuncio {
    var idAnuncio: String = ""
    var uid: String = ""
    var marca: String = ""
    var categoria: String = ""
    var condicion: String = ""
    var precio: String = ""
    var titulo: String = ""
    var descripcion: String = ""
    var timestamp: String = ""
    var estado: String = ""

    constructor()

    constructor(
        idAnuncio: String,
        uid: String,
        marca: String,
        categoria: String,
        condicion: String,
        precio: String,
        titulo: String,
        descripcion: String,
        timestamp: String,
        estado: String
    ) {
        this.idAnuncio = idAnuncio
        this.uid = uid
        this.marca = marca
        this.categoria = categoria
        this.condicion = condicion
        this.precio = precio
        this.titulo = titulo
        this.descripcion = descripcion
        this.timestamp = timestamp
        this.estado = estado
    }
}