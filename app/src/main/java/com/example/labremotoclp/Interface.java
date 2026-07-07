package com.example.labremotoclp;

import java.util.LinkedHashMap;
import java.util.Map;

public class Interface {
    public final static int STATUS_DESABILITADO = 0;
    public final static int STATUS_ATIVADO = 1;

    // Exact pin mapping from ESP32 C++ code:
    // const uint8_t pinos_entradas_digitais[10] = { 36, 39, 34, 35, 0, 2, 5, 12, 15, 14 };
    private final static String[] INPUT_KEYS = {
        "Input_36", "Input_39", "Input_34", "Input_35", "Input_0", 
        "Input_2", "Input_5", "Input_12", "Input_15", "Input_14"
    };

    // const uint8_t pinos_saidas_digitais[10]   = { 4, 18, 19, 25, 26, 32, 33, 23, 13, 27 };
    private final static String[] OUTPUT_KEYS = {
        "Output_4", "Output_18", "Output_19", "Output_25", "Output_26",
        "Output_32", "Output_33", "Output_23", "Output_13", "Output_27"
    };

    private final static Map<String, Object> statusInterfaces = new LinkedHashMap<>();

    static {
        for (String key : INPUT_KEYS) statusInterfaces.put(key, STATUS_DESABILITADO);
        for (String key : OUTPUT_KEYS) statusInterfaces.put(key, STATUS_DESABILITADO);
        statusInterfaces.put("Temp_A0", 0.0f);
        statusInterfaces.put("CLP_A1", 0.0f);
        statusInterfaces.put("CLP_A2", 0.0f);
        statusInterfaces.put("CLP_A3", 0.0f);
        statusInterfaces.put("DAC_Ch0", 0.0f);
        statusInterfaces.put("DAC_Ch1", 0.0f);
        statusInterfaces.put("DAC_Ch2", 0.0f);
        statusInterfaces.put("DAC_Ch3", 0.0f);
        // Inverter Status
        statusInterfaces.put("inv_f", 0.0f);
        statusInterfaces.put("inv_r", 0);
        statusInterfaces.put("inv_a", 0.0f);
        statusInterfaces.put("inv_s", "AVANCO");
        statusInterfaces.put("inv_w", "0x0000");
        // Diagnostics
        statusInterfaces.put("wifi", 0);
        statusInterfaces.put("heap", 0);
    }

    public static void atualizarInterface(String nomeInterface, Object valor){
        statusInterfaces.put(nomeInterface, valor);
    }

    public static String[] getInputKeys() { return INPUT_KEYS; }
    public static String[] getOutputKeys() { return OUTPUT_KEYS; }

    public static String converteParaJson(){
        StringBuilder retorno = new StringBuilder("{");
        int cont = 0;
        for (Map.Entry<String, Object> entry : statusInterfaces.entrySet()) {
            retorno.append("\"").append(entry.getKey()).append("\"").append(":");
            if (entry.getValue() instanceof String) {
                retorno.append("\"").append(entry.getValue()).append("\"");
            } else {
                retorno.append(entry.getValue());
            }
            cont++;
            if(cont != statusInterfaces.size()) {
                retorno.append(",");
            }
        }
        retorno.append("}");
        return retorno.toString();
    }
}
