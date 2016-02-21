package net.zomis.gameai;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class HumanPlayer extends GameAI {

    private final Scanner scanner;

    public HumanPlayer(String name, Scanner scanner) {
        super(name);
        this.scanner = scanner;
    }

    @Override
    public GameMove makeMove(Random random, GameMove[] moves) {
        System.out.println("Choose your move:");
        List<GameMove> allowedMoves = Arrays.stream(moves)
            .filter(GameMove::isAllowed)
            .collect(Collectors.toList());

        int choiceIndex;
        do {
            for (int i = 0; i < allowedMoves.size(); i++) {
                System.out.println((i + 1) + ". " + allowedMoves.get(i));
            }
            String choice = scanner.nextLine();
            try {
                choiceIndex = Integer.parseInt(choice);
            } catch (NumberFormatException ex) {
                choiceIndex = -1;
            }
        } while (choiceIndex < 0 || choiceIndex >= allowedMoves.size());

        GameMove move = allowedMoves.get(choiceIndex - 1);
        System.out.println("You choose " + choiceIndex + ": " + move);
        move.perform();
        return move;
    }

}
