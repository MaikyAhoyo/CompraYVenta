package com.example.appcomprayventa.modelos

class Usuario {
    //Atributos
    var uid: String = ""
    var nombres: String = ""
    var email: String = ""
    var imagen: String = ""

    //constructor vacio
    constructor()

    //constructor con atributos
    constructor(uid: String, nombres: String, email: String, imagen: String) {
        this.uid = uid
        this.nombres = nombres
        this.email = email
        this.imagen = imagen
    }
}