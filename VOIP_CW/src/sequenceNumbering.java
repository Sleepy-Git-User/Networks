public class sequenceNumbering {

    //Send in packets - Packet structure needs to have built in redundant stream
    //Store in 4x4 grid
    //Interleave packets

    public static void main(String[] args) {

        int[] test = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        int[] test2 = new int[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j <4; j++) {
                test2[4 * (3 - j) + i] = test[4 * i + j];
            }

        }
        for (int i = 0; i < test2.length; i++) {
            System.out.println(test2[i]);
        }


    }





}
