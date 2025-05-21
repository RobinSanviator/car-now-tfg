package com.example.carnowapp.utilidad;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;

import androidx.core.content.ContextCompat;

import com.example.carnowapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ValidacionUtilidad {

    public static boolean validarCampoVacio(TextInputLayout layout, TextInputEditText campo, String mensajeError) {
        String texto = campo.getText() != null ? campo.getText().toString().trim() : "";
        if (texto.isEmpty()) {
            layout.setError(mensajeError);
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public static boolean validarEmail(TextInputLayout layout, TextInputEditText campo, String mensajeErrorVacio, String mensajeErrorFormato) {
        String email = campo.getText() != null ? campo.getText().toString().trim() : "";
        if (email.isEmpty()) {
            layout.setError(mensajeErrorVacio);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layout.setError(mensajeErrorFormato);
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public static boolean validarConfirmacionContrasena(TextInputLayout layout, String contrasena, String confirmarContrasena, Context contexto) {
        if (confirmarContrasena.isEmpty()) {
            layout.setError(contexto.getString(R.string.confirmar_contrasena));
            return false;
        } else if (!contrasena.equals(confirmarContrasena)) {
            layout.setHelperText(contexto.getString(R.string.error_contrasenas_no_coinciden));
            layout.setHelperTextColor(ColorStateList.valueOf(ContextCompat.getColor(contexto, R.color.color_error)));
            layout.setError(null); // Asegura que no se muestre error junto a helperText
            return false;
        } else {
            layout.setHelperText(null);
            layout.setError(null);
            return true;
        }
    }

    public static void configurarMostrarContrasenaInicio(TextInputEditText etContrasena, TextInputLayout tilContrasena, Context contexto) {
        etContrasena.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    tilContrasena.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                } else {
                    tilContrasena.setEndIconMode(TextInputLayout.END_ICON_NONE);
                }
            }
        });
    }


    public static void configurarMostrarContrasenaRegistro(TextInputEditText etContrasena,
                                                           TextInputLayout tilContrasena,
                                                           TextInputEditText etConfirmarContrasena,
                                                           TextInputLayout tilConfirmarContrasena,
                                                           Context contexto) {

        etContrasena.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String contrasena = s.toString();
                if (!contrasena.isEmpty()) {
                    tilContrasena.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                    if (contrasena.length() < 6) {
                        tilContrasena.setHelperText(contexto.getString(R.string.error_contrasena_min_6));
                        tilContrasena.setHelperTextColor(
                                ColorStateList.valueOf(ContextCompat.getColor(contexto, R.color.color_error))
                        );
                    } else {
                        tilContrasena.setHelperText(null);
                    }
                } else {
                    tilContrasena.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tilContrasena.setHelperText(null);
                }
            }
        });

        etConfirmarContrasena.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    tilConfirmarContrasena.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                } else {
                    tilConfirmarContrasena.setEndIconMode(TextInputLayout.END_ICON_NONE);
                }
            }
        });
    }
}
