package nc.omegapower.batterie_connectee_api.utils;

import java.util.List;

public class Decodeur {

    public static class BatterieDecodee {
        public int soc;
        public boolean alarme;
        public float tension;
        public float courant;
        public float temperature;
    }

    public static BatterieDecodee decoder(List<Integer> trame) {
        if (trame == null || trame.size() != 5) {
            throw new IllegalArgumentException("La trame doit contenir exactement 5 octets");
        }

        // Conversion de la liste en tableau d'octets
        int[] b = new int[5];
        for (int i = 0; i < 5; i++) {
            b[i] = trame.get(i) & 0xFF;
        }

        BatterieDecodee m = new BatterieDecodee();

        // 1. SOC et Alarme
        m.soc = b[0] >> 1;
        m.alarme = (b[0] & 1) == 1;

        // 2. Tension (12 bits)
        int tensionBrute = (b[1] << 4) | (b[2] >> 4);
        m.tension = tensionBrute * 0.02f;

        // 3. Courant (12 bits signé)
        int courantBrut = ((b[2] & 0x0F) << 8) | b[3];
        if ((courantBrut & 0x800) != 0) { // Si le bit de signe est à 1
            courantBrut -= 4096;
        }
        m.courant = courantBrut * 0.1f;

        // 4. Température
        m.temperature = (b[4] * 0.5f) - 20.0f;

        return m;
    }
}