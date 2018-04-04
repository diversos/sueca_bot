import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

class Main {
    private static Input in;

    static class Card {
        char rank, suit;

        public Card(String s) {
            if (s.equals("X"))
                rank = suit = 'X';
            else {
                rank = s.charAt(0);
                suit = s.charAt(1);
            }
        }

        boolean empty() {
            return rank == 'X' || suit == 'X';
        }
    }

    static class Trick {
        Card cards[];
        int startingPlayer;
        char suit;

        public Trick() {
            cards = new Card[4];
        }
    }

    static class Input {
        int currentPlayer;
        List<Card> cardsInHand;
        int trumpPlayer;
        Card trumpCard;
        Trick currentTrick;
        List<Trick> previousTricks;
        int points[];

        public Input() {
            points = new int[2];
            cardsInHand = new ArrayList<>();
            previousTricks = new ArrayList<>();
            currentTrick = new Trick();
        }
    }

    public static Input readInput() {
        Scanner in = new Scanner(System.in);
        Input input = new Input();
        input.currentPlayer = in.nextInt();
        int NC = in.nextInt();
        for (int i = 0; i < NC; i++)
            input.cardsInHand.add(new Card(in.next()));
        input.trumpPlayer = in.nextInt();
        input.trumpCard = new Card(in.next());
        input.currentTrick.startingPlayer = in.nextInt();
        for (int i = 0; i < 4; i++)
            input.currentTrick.cards[i] = new Card(in.next());
        input.currentTrick.suit = in.next().charAt(0);
        int PT = in.nextInt();
        for (int i = 0; i < PT; i++) {
            Trick t = new Trick();
            t.startingPlayer = in.nextInt();
            for (int j = 0; j < 4; j++)
                t.cards[j] = new Card(in.next());
            t.suit = t.cards[t.startingPlayer].suit;
            input.previousTricks.add(t);
        }
        input.points[0] = in.nextInt();
        input.points[1] = in.nextInt();
        return input;
    }

    private static int rankToPosition(char suit){
        int value;

        switch(suit){
            case 'A':
                value = 0;
                break;
            case '7':
                value = 1;
                break;
            case 'K':
                value = 2;
                break;
            case 'J':
                value = 3;
                break;
            case 'Q':
                value = 4;
                break;
            default:
                value = 11 - Character.getNumericValue(suit);
        }

        return value;
    } // rankToPosition

    private static char positionToRank(int pos){
        char value;

        switch(pos){
            case 0:
                value = 'A';
                break;
            case 1:
                value = '7';
                break;
            case 2:
                value = 'K';
                break;
            case 3:
                value = 'J';
                break;
            case 4:
                value = 'Q';
                break;
            default:
                value = (char) (Character.forDigit(11-pos, 10));
        }

        return value;
    } // positionToRank

    private static int suitToPosition(char suit){
        int value;

        switch(suit){
            case 'H':
                value = 0;
                break;
            case 'S':
                value = 1;
                break;
            case 'D':
                value = 2;
                break;
            case 'C':
                value = 3;
                break;
            default:
                value = -1;
        }

        return value;
    } // suitToPosition

    private static char positionToSuit(int pos){
        char value = 'Z';
        
        switch(pos){
            case 0:
                value = 'H';
                break;
            case 1:
                value = 'S';
                break;
            case 2:
                value = 'D';
                break;
            case 3:
                value = 'C';
                break;
        }
        return value;
    } // positionToSuit

    private static int[] fillsCards(char suit){
        int[] suitDeck = new int[12];
        int myCards = 0;
        int playedCards = 0;
        int partner = (in.currentPlayer + 2) % 4;

        for(Card c: in.cardsInHand){
            if(c.suit == suit){
                suitDeck[rankToPosition(c.rank)] = 1;
                myCards++;
            } // if
        } // for

        suitDeck[10] = myCards;


        for(Trick t: in.previousTricks) {
            for (Card c : t.cards) {
                if (c.suit == suit) {
                    suitDeck[rankToPosition(c.rank)] = 2;
                    playedCards++;
                } // if
            } // for
        } // for

        for(int i=0; i<4; i++){
            Card c = in.currentTrick.cards[i];
            if (c.suit == suit) {
                if(partner == i){
                    suitDeck[rankToPosition(c.rank)] = 4;
                } // if
                else {
                    suitDeck[rankToPosition(c.rank)] = 3;
                } // else
                playedCards++;
            } // if
        } // for

        suitDeck[11] = playedCards;

        return suitDeck;
    } // fillCards

    private static char biggestCard(int[] suit){
        if(suit[10] == 0 || suit[11] == 10)
            return 'N';

        for(int i=0; i<10; i++){
            if(suit[i] == 1){
                return positionToRank(i);
            } // if
        } // for

        return 'Z'; // nunca acontece
    } // biggestCard

    private static char smallestCard(int[] suit){
        if(suit[10] == 0 || suit[11] == 10)
            return 'N';

        for(int i=9; i>=0; i--){
            if(suit[i] == 1){
                return positionToRank(i);
            } // if
        } // for

        return 'Z'; // nunca acontece
    } // biggestCard

    private static boolean isBigger(int[] suit){
        if(suit[10] == 0 || suit[11] == 10)
            return false;

        for(int i=0; i<suit.length; i++){
            if(suit[i] == 1 || suit[i] == 4){    // own or partner
                return true;
            } // if
            else if(suit[i] == 0 || suit[i] == 3){      // not played or rival
                return false;
            } // else if
        } // for

        return false; // nunca acontece
    } // isBigger

    private static boolean isValidPlay(List<Card> cards, char suit, char rank) {
        for(Card c: cards){
            if(c.suit == suit){
                if(c.rank == rank){
                    return true;
                } // if
            } // if
        } // for

        return false;
    } // isValidPlay

    private static boolean roundHasTrump(){
        for(Card c: in.currentTrick.cards){
            if(c.suit == in.trumpCard.suit)
                return true;
        } // for

        return false;
    } // roundHasTrump

    public static Card play() {
        // introduz o teu codigo aqui
        Trick currentTrick = in.currentTrick;
        List<Card> playable = new ArrayList<>();
        Card card = null;

        // A, 7, K, D, J, 6, 5, 4, 3, 2, #myCards [10], #playedCards [11]
        // #playedCards = 2, #myCards = 1, #otherPlayersCards = 0
        List<int[]> suits = new ArrayList<>();
        suits.add(fillsCards('H'));
        suits.add(fillsCards('S'));
        suits.add(fillsCards('D'));
        suits.add(fillsCards('C'));


        // #### MISSING TRUMP CARD

        // is the first one playing
        if(in.currentPlayer == currentTrick.startingPlayer){
            playable = in.cardsInHand;
            // ver suits com menor # de cartas
            // ver se tem a maior carta possivel
            // caso não tenha ir para o próximo suit menor
            // caso não tenha nenhuma carta maior escolher a menor carta do menor suit. ## Tentar escolher uma carta que não dê pontos // não seja trunfo ### mais logo tlvx

            card = smallestSuit(suits, new int[4], 0);

            if(!isValidPlay(playable, card.suit, card.rank)){
                card = playable.get(ThreadLocalRandom.current().nextInt(0, playable.size()));
            }


        } // if
        // has to assist if it has a card of the same suit
        else{
            char currentSuit = currentTrick.cards[currentTrick.startingPlayer].suit;

            for(Card c: in.cardsInHand){
                if(c.suit == currentSuit){
                    playable.add(c);
                    card = c;
                } // if
            } // for

            if(playable.isEmpty()) { // igual ao caso de ser o primeiro a jogar por agora ### mudar para tentar jogar trunfo primeiro, ao contrário do primeiro a jogar
                playable = in.cardsInHand;
                if(roundHasTrump()){
                    if(isBigger(suits.get(suitToPosition(in.trumpCard.suit)))){
                        StringBuilder sb = new StringBuilder();
                        char c = biggestCard(suits.get(suitToPosition(in.trumpCard.suit)));
                        if(c == 'N') {
                            return null;
                        } // if

                        sb.append(c);
                        sb.append(in.trumpCard.suit);
                        card = new Card(sb.toString());
                    } // if
                    else{
                        card = smallestSuitWithoutTrump(suits, new int[4], 0);
                    } // else
                } // if
                else{
                    // round doesn't have trump card
                    // play highest trump for now (if it's bigger). if it's last player play lower trump

                    if(!((in.currentPlayer + 1) % 4 == in.currentTrick.startingPlayer) && isBigger(suits.get(suitToPosition(in.trumpCard.suit)))){
                        StringBuilder sb = new StringBuilder();
                        char c = biggestCard(suits.get(suitToPosition(in.trumpCard.suit)));
                        if(c == 'N') {
                            return null;
                        } // if

                        sb.append(c);
                        sb.append(in.trumpCard.suit);
                        card = new Card(sb.toString());
                    } // if
                    else{
                        StringBuilder sb = new StringBuilder();
                        sb.append(smallestCard(suits.get(suitToPosition(in.trumpCard.suit))));
                        sb.append(in.trumpCard.suit);
                        card =  new Card(sb.toString());
                    } // else

                } // else
                //card = smallestSuit(suits, new int[4], 0);
            } // if
            else{       // tem que assistir
                if(roundHasTrump()){
                    StringBuilder sb = new StringBuilder();
                    sb.append(smallestCard(suits.get(suitToPosition(currentTrick.suit))));
                    sb.append(currentTrick.suit);
                    card =  new Card(sb.toString());
                } // if
                else{
                    if(isBigger(suits.get(suitToPosition(currentTrick.suit)))){
                        StringBuilder sb = new StringBuilder();
                        char c = biggestCard(suits.get(suitToPosition(currentTrick.suit)));
                        if(c == 'N') {
                            return null;
                        } // if

                        sb.append(c);
                        sb.append(currentTrick.suit);
                        card = new Card(sb.toString());
                    } // if
                    else{
                        StringBuilder sb = new StringBuilder();
                        sb.append(smallestCard(suits.get(suitToPosition(currentTrick.suit))));
                        sb.append(currentTrick.suit);
                        card =  new Card(sb.toString());
                    } // else
                } // else

                // enviar array de naipe para assistir
                // enviar mais alto/mais baixo
            } // else

        } // else

        if(card == null || !isValidPlay(playable, card.suit, card.rank)){
            card = playable.get(ThreadLocalRandom.current().nextInt(0, playable.size()));
        }

        return card;
    } // play


    private static Card smallestSuitWithoutTrump(List<int[]> suits, int[] filter, int nFilters){
        int smallest = 11;
        int smallestSuit = -1;
        int temp;
        Card card = null;

        if(nFilters == 4)
            return null;

        for(int i=0; i<4; i++){
            if(i == suitToPosition(in.trumpCard.suit)){
                // ignores trump deck
                continue;
            } // if
            if(filter[i] == 0){
                temp = suits.get(i)[10];
                if(temp > 0 && temp < smallest){
                    smallest = temp;
                    smallestSuit = i;
                } // if
            } // if
        } // for

        if(smallestSuit == -1)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append(smallestCard(suits.get(smallestSuit)));
        sb.append(positionToSuit(smallestSuit));
        card =  new Card(sb.toString());

        return card;
    } // smallestSuitWithoutTrump


    private static Card smallestSuit(List<int[]> suits, int[] filter, int nFilters){
        int smallest = 11;
        int smallestSuit = -1;
        int temp;
        Card card = null;

        if(nFilters == 4)
            return null;

        for(int i=0; i<4; i++){
            if(filter[i] == 0){
                temp = suits.get(i)[10];
                if(temp > 0 && temp < smallest){
                    smallest = temp;
                    smallestSuit = i;
                } // if
            } // if
        } // for

        if(smallestSuit == -1)
            return null;
        
        if(isBigger(suits.get(smallestSuit))){
            StringBuilder sb = new StringBuilder();
            char c = biggestCard(suits.get(smallestSuit));
            if(c == 'N') {
                return null;
            } // if

            sb.append(c);
            sb.append(positionToSuit(smallestSuit));
            card = new Card(sb.toString());
        } // if
        else{
            filter[smallestSuit] = 1;
            card = smallestSuit(suits, filter, nFilters+1);
            if(card == null && nFilters == 0){
                StringBuilder sb = new StringBuilder();
                sb.append(smallestCard(suits.get(smallestSuit)));
                sb.append(positionToSuit(smallestSuit));
                card =  new Card(sb.toString());
            } // if

        } // else

        return card;
    } // smallestSuit

    public static void main(String[] args) {
        in = readInput();
        Card c = play();
        System.out.println("" + c.rank + c.suit);
    }
}
