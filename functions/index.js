const functions = require("firebase-functions");
const { defineSecret } = require("firebase-functions/params");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");
// Inicializa Firebase Admin
admin.initializeApp();

// Define los secrets
const gmailEmail = defineSecret("GMAIL_EMAIL");
const gmailPassword = defineSecret("GMAIL_PASSWORD");

// Define la función que se ejecuta al crear un usuario
exports.sendWelcomeEmail = functions.auth.user().onCreate(
  {
    region: "europe-west1",
    secrets: [gmailEmail, gmailPassword],
  },
  (event) => {
    const user = event.data;

    const transporter = nodemailer.createTransport({
      service: "gmail",
      auth: {
        user: gmailEmail.value(),
        pass: gmailPassword.value(),
      },
    });

    const mailOptions = {
      from: "CarNow <carnowapp.dev@gmail.com>",
      to: user.email,
      subject: "¡Bienvenido a CarNow!",
      html: `<h1>Hola ${user.displayName || "Usuario"}, gracias por registrarte</h1>`,
    };

    return transporter.sendMail(mailOptions)
      .then(() => console.log("Correo enviado a:", user.email))
      .catch((error) => console.error("Error al enviar correo:", error));
  }
);

  
