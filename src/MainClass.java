import java.util.Scanner;

public class MainClass {
    private static final int MINIMAX = 111;
    private static final int MINIMAX_ALPHA_BETA = 222;
    private static final int MINIMAX_PV = 333;
    private static final int MINIMAX_PV_WITH_NULL_WINDOW = 444;

    // testing
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        Client client;
        int userChoice = MINIMAX_ALPHA_BETA;

        System.out.println("Please choose algorithm:\nDefault - (0)\nMinimax - (1)\nMinimax with Alpha-Beta - (2)" +
                "\nMinimax with Principal Variation (PV) Search - (3)" +
                "\nMinimax with Principal Variation (PV) and Null-Window (NW) Search - (4)\nYour choice: ");
        int userInput = in.nextInt();

        userChoice = inputToCodec(userInput);

        if(args.length == 1)
            client = new Client(Integer.parseInt(args[0]), userChoice);
        else
            client = new Client(10, userChoice);

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
