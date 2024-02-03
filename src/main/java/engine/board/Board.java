package engine.board;

public class Board {

    private final int width;
    private final int height;

    //Probability of a given box to have a bomb. 1f is 100% and 0f 0%
    private final float probability = 0.1f;

    /*
    '-' for null, '*' bor bomb, and 'n' for the number of surrounding bombs
     */
    private char[][] boxesHidden;
    private boolean[][] boxesVisible;

    private int totalMines;
    private int revealedBoxes;

    public Board(int width, int height) throws IllegalArgumentException{
        if(width <= 0 || height <= 0){
            throw new IllegalArgumentException("Width and heigh have to be bigger than 0.");
        }
        this.width = width;
        this.height = height;

        boxesHidden = new char[width][height];
        boxesVisible = new boolean[width][height];

        setup(probability);
        calculateAdjacentMines();
    }

    private void setup(float prob){
        float random;
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                random = (float) Math.random();
                if(random < prob) totalMines++;

                boxesHidden[i][j] = (random < prob) ? '*' : '-';
                boxesVisible[i][j] = false;
            }
        }

    }

    /**
     *
     * @return Returns false if a bomb was found.
     */
    public boolean reveal(int x, int y) throws IndexOutOfBoundsException {
        if (x < 0 || x > width || y < 0 || y > height) throw new IndexOutOfBoundsException();

        if (boxesHidden[x][y] == '*') {
            boxesVisible[x][y] = true;
            return false;
        }

        revealCell(x,y);
        return true;
    }

    public char[][] getBoxesHidden() {
        return boxesHidden;
    }

    public boolean[][] getBoxesVisible() {
        return boxesVisible;
    }

    private boolean revealCell(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;
        if (!boxesVisible[x][y]) {
            revealedBoxes++;
            boxesVisible[x][y] = true;

            if (boxesHidden[x][y] == '0') {
                // If the cell has no adjacent mines, reveal neighboring cells recursively
                for (int i = x - 1; i <= x + 1; i++) {
                    for (int j = y - 1; j <= y + 1; j++) {
                        revealCell(i, j);
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void calculateAdjacentMines() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (boxesHidden[i][j] == '-') {
                    int count = countAdjacentMines(i, j);
                    boxesHidden[i][j] = (char) (count + '0');
                }
            }
        }
    }

    private int countAdjacentMines(int x, int y) {
        int count = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < width && j >= 0 && j < height && boxesHidden[i][j] == '*') count++;
            }
        }
        return count;
    }

    public boolean hasFinished(){
        return totalMines >= (boxesVisible.length * boxesVisible[0].length) - revealedBoxes;
    }

}
