
public class MainClass {
    private static final int MINIMAX = 111;
    private static final int MINIMAX_ALPHA_BETA = 222;
    private static final int MINIMAX_PV = 333;
    private static final int MINIMAX_PV_WITH_NULL_WINDOW = 444;

    // testing
    public static void main(String[] args){
        Client client;


        if(args.length == 2)
            client = new Client(Integer.parseInt(args[0]), inputToCodec(Integer.parseInt(args[1])));
        else
            client = new Client(10, Integer.parseInt(args[0]));

        // send the first message - my name
        client.sendName();

        // start receiving messages;
        client.receiveMessages();
    }

    private static int inputToCodec(int input){

        if(input == 0 || input == 2)
            return MINIMAX_ALPHA_BETA;
        else if(input == 1)
            return MINIMAX;
        else if(input == 3)
            return MINIMAX_PV;
        else
            return MINIMAX_PV_WITH_NULL_WINDOW;
    }

}
