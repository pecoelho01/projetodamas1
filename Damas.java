import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.print.attribute.standard.JobMessageFromOperator;

record Position(int line, int col) {

	// Verifica que a posição, ou seja, a peça está dentro do tablueiro
	Position
	{
		assert (line >= 0 && line <= 7) && (col >= 0 && col <= 7);
	}
}

//Os atributos para a realização do jogo 
// Int size: tamanho do tabuleiro
// Position [] symbols: para colcoar a posições das peças
// Boolean [] isWhite: para no mesmo "index" que o symbols verificar se a peça é
// branca ou não, se for é true, se não é false
// Boolean WhiteTurn: verificar de quem é a vez

class Damas {
	private int size;
	private int piecesperplayer;
	private Position[] symbols;
	private boolean[] isWhite;
	private boolean WhiteTurn;
	private int used;
	private int moveCount;

	// Constutor no início do jogo, ele chama o procedimento "new game" com uma
	// espécie de default: de 8x8 c/ 12 peças para cada player
	Damas() {
		newgame(8, 12);
	}
	
	// Construtor para usar quando carrega um jogo já previamente gravado 
	Damas(int size, Position [] symbols1, boolean [] isWhite1, boolean WhiteTurn1, int movecount) {
		this.size = size; 
		this.isWhite = isWhite1;
		this.symbols = symbols1; 
		this.WhiteTurn = WhiteTurn1;
		this.used = symbols.length;
		this.moveCount = movecount;
	}
	
	// Construtor para quando o utilizador quer fazer um novo jogo 
	Damas (int size, int piecesperplayer){
		this.size = size;
		this.piecesperplayer = piecesperplayer;
		newgame(size, piecesperplayer);
	}
	

	// O procedimento que faz a iniciação do jogo conforme o utilizador quiser
	void newgame(int size, int piecesperplayer) {
		this.piecesperplayer = piecesperplayer;
		this.size = size;
		symbols = new Position[2 * piecesperplayer];
		isWhite = new boolean[2 * piecesperplayer];
		used = 0;
		moveCount = 0;

		int maxpiecesperplayer = (size * size) / 2;

		if (piecesperplayer > maxpiecesperplayer) {
			piecesperplayer = maxpiecesperplayer;
		}

		
		for (int line = 0; line < size / 2; line++) {
			for (int col = 0; col < size; col++) {
				if ((line + col) % 2 != 0 && (used) < piecesperplayer) {
					symbols[used] = new Position(line, col);
					isWhite[used] = false;
					used++;
				}
			}
		}
		for (int line = size - 1; line >= size / 2; line--) {
			for (int col = 0; col < size; col++) {
				if ((line + col) % 2 != 0 && (used) < (2 * piecesperplayer)) {
					symbols[used] = new Position(line, col);
					isWhite[used] = true;
					;
					used++;
				}
			}
		}
		this.WhiteTurn = true;
	}
	Position [] symbols() {
		return symbols;
	}

	int size() {
		return size;
	}

	int pieces() {
		return piecesperplayer;
	}

	int getUsed() {
		return used;
	}
	boolean [] isWhite() {
		return isWhite;
	}

	Position getSymbol(int index) {
		if (index >= 0 && index < used) {
			return symbols[index];
		}
		return null;
	}

	void increment() {
		moveCount++;
	}

	int getMoveCOunt() {
		return moveCount;
	}

	boolean isWhite(int i) {
		return isWhite[i];
	}
	

// isVisited diz-me que dando uma posição se ela já tá ou não ocupada por uma peça 

	boolean isVisited(Position p) {
		for (int i = 0; i < used; i++) {
			if (symbols[i].equals(p))
				return true;
		}
		return false;
	}

// isWhite serve-me para dizer se a peça é branca ou preta. Comparando o index do vetor symbols e isWhite se no index 5, p.e., tiver true no isWhite ela é branca.
	boolean isWhite(Position p) {
		for (int i = 0; i < used; i++) {
			if (symbols[i].equals(p)) {
				return isWhite[i];
			}
		}
		return false;
	}

	boolean isWhiteTurn() {
		return WhiteTurn;
	}

// isEmpty verifica se a dada posição está ou não vazia

	boolean isEmpty(Position pos) {
		for (int i = 0; i < used; i++) {
			if (symbols[i].equals(pos)) {
				return false; // Não está vazia
			}
		}
		return true; // Está vazia
	}

// isValidMove indica se o movimento é válido: se está dentro do tabuleiro, se a posição para onde está vazia

	boolean isValidMove(Position from, Position to) {
		if (to.line() < 0 || to.line() >= size || to.col() < 0 || to.col() >= size) {
			return false; // Fora do tabuleiro
		}
		if (!isEmpty(to)) {
			return false; // A posição de destino já está ocupada
		}

		int lineDiff = Math.abs(to.line() - from.line());
		int colDiff = Math.abs(to.col() - from.col());

		// O movimento deve ser uma casa ou duas casas diagonalmente
		if (lineDiff != colDiff || (lineDiff != 1 && lineDiff != 2)) {
			return false;
		}

		// Verifica se a peça branca não sobe e a preta não desce
		if (isWhite(from)) {
			if (to.line() >= from.line()) { // Peça branca não pode mover para baixo
				return false;
			}
		} else {
			if (to.line() <= from.line()) { // Peça preta não pode mover para cima
				return false;
			}
		}

		return true;
	}

// Este procedimento serve para jogar, no fundo. 
	void play(Position from, Position to) {

		// Verificar mudança de turno

		if ((WhiteTurn && !isWhite(from)) || (!WhiteTurn && isWhite(from))) {
			return; // Não é a vez da cor da peça
		}

		int linediff = to.line() - from.line();
		int coldiff = to.col() - from.col();

		// Para onde pode ir a branca
		if (isWhite(from)) {
			if ((linediff != -1 && coldiff != -1) || (linediff != -1 && coldiff != 1)) {
				return;
			} // Para onde pode ir a preta
		} else {
			if ((linediff != 1 && coldiff != -1) || (linediff != 1 && coldiff != 1)) {
				return;
			}
		}

		// Substitui no vetor
		for (int i = 0; i < used; i++) {
			if (symbols[i].equals(from)) {
				symbols[i] = to;
				WhiteTurn = !WhiteTurn;
				break;
			}
		}
		increment();
	}

// Função para comer. Garante a mudança de turno. 
	void ToEat(Position from, Position to) {
		// Verifica se o movimento está dentro do turno correto
		if ((WhiteTurn && !isWhite(from)) || (!WhiteTurn && isWhite(from))) {
			return; // Não é a vez da cor da peça, então não faz nada
		}

		int linediff = Math.abs(to.line() - from.line());
		int coldiff = Math.abs(to.col() - from.col());

		// Verifica se o movimento é válido (exatamente 2 casas na diagonal)
		if (linediff == 2 && coldiff == 2) {
			int middleline = (from.line() + to.line()) / 2;
			int middlecol = (from.col() + to.col()) / 2;
			Position middle = new Position(middleline, middlecol);

			// Verifica se a posção final está vazia e se a posição do meio tem uma peça
			// adversária
			if (isEmpty(to) && isVisited(middle) && isWhite(middle) != isWhite(from)) {
				// Verifica a direção do movimento: brancas não podem capturar para baixo e
				// pretas não para cima
				if (isWhite(from)) {
					// Peças brancas não podem capturar para baixo
					if (to.line() >= from.line()) {
						return; // Movimento inválido
					}
				} else {
					// Peças pretas não podem capturar para cima
					if (to.line() <= from.line()) {
						return; // Movimento inválido
					}
				}

				// Move a peça para o destino
				for (int i = 0; i < used; i++) {
					if (symbols[i].equals(from)) {
						symbols[i] = to;
						break;
					}
				}

				// Remove a peça capturada e alterna o turno
				removecapture(middle);
				WhiteTurn = !WhiteTurn;
				increment();
			}
		}
	}

// Valida se é possível comer
	boolean isValideEat(Position from, Position to) {
		// Verificar se o destino está dentro do tabuleiro
		if (to.line() < 0 || to.line() >= size || to.col() < 0 || to.col() >= size) {
			return false;
		}

		int lineDiff = to.line() - from.line();
		int colDiff = Math.abs(to.col() - from.col());

		// Verificar se o movimento é uma captura válida
		if (Math.abs(lineDiff) == 2 && colDiff == 2) {
			int middleLine = (from.line() + to.line()) / 2;
			int middleCol = (from.col() + to.col()) / 2;
			Position middle = new Position(middleLine, middleCol);

			// Verificar direção válida e peça no meio
			if (isVisited(middle) && isEmpty(to)) {
				if (isWhite(from) && !isWhite(middle) && lineDiff == -2) {
					// Branca só pode capturar para cima
					return true;
				}
				if (!isWhite(from) && isWhite(middle) && lineDiff == 2) {
					// Preta só pode capturar para baixo
					return true;
				}
			}
		}
		return false; // Não é um movimento de captura válido
	}

// Procedimento para removar a peça que foi comida 

	void removecapture(Position middle) {
		for (int i = 0; i < used; i++) {
			if (symbols[i].equals(middle)) {
				symbols[i] = symbols[used - 1];
				isWhite[i] = isWhite[used - 1];
				used--;
				break;
			}
		}
	}

// Contar quantas peças há
	int CountWhitePieces() {
		int CountWhite = 0;
		for (int i = 0; i < used; i++) {
			if (isWhite[i] == true) {
				CountWhite = CountWhite + 1;
			}
		}
		return CountWhite;
	}

// Contar quantas peças pretas há 
	int CountBlackPieces() {
		return (used) - CountWhitePieces();

	}

// Verifica no tabuleiro se há num determinado momento se há capturas possíveis 
	boolean CaptureAvailable() {
		int i = 0;
		while (i < getUsed()) {
			Position piece = getSymbol(i);

			// Verifica se a peça está na vez correta (branca ou preta)
			if ((isWhite(piece) && isWhiteTurn()) || (!isWhite(piece) && !isWhiteTurn())) {

				// As possíveis capturas
				Position[] possibleCaptures = { new Position(piece.line() - 2, piece.col() + 2),
						new Position(piece.line() - 2, piece.col() - 2),
						new Position(piece.line() + 2, piece.col() + 2),
						new Position(piece.line() + 2, piece.col() - 2) };

				// Verifica se é possível realizar uma captura
				int j = 0;
				while (j < possibleCaptures.length) {
					Position to = possibleCaptures[j];
					if (isValideEat(piece, to)) {
						return true;
					}
					j++;
				}
			}
			i++;
		}

		return false;
	}

	boolean ItsOver() {
		if (CountWhitePieces() == 0 || CountBlackPieces() == 0) {
			return true;
		}

		int i = 0;
		while (i < getUsed()) {
			Position piece = getSymbol(i);
			if ((isWhite(piece) && isWhiteTurn()) || (!isWhite(piece) && !isWhiteTurn())) {
				if (isValidMove(piece, new Position(piece.line() - 1, piece.col() + 1))
						|| isValidMove(piece, new Position(piece.line() - 1, piece.col() - 1))
						|| isValidMove(piece, new Position(piece.line() + 1, piece.col() + 1))
						|| isValidMove(piece, new Position(piece.line() + 1, piece.col() - 1))) {
					return false;
				}
				if (isValideEat(piece, new Position(piece.line() - 2, piece.col() + 2))
						|| isValideEat(piece, new Position(piece.line() - 2, piece.col() - 2))
						|| isValideEat(piece, new Position(piece.line() + 2, piece.col() + 2))
						|| isValideEat(piece, new Position(piece.line() + 2, piece.col() - 2))) {
					return false;
				}

			}
			i++;
		}
		return true;
	}

	void save(String fileName) {
		try { // Vai tentar correr o código, se houve uma exceção vai para o catch
			PrintWriter writer = new PrintWriter(new File(fileName)); // Vai criar um ficheiro para escever lá dentro 
			writer.println(used);
			writer.println(size);
			writer.println(WhiteTurn);
			writer.println(moveCount);
			
			for (int i = 0; i < used; i++) {
					writer.println(symbols[i].line() + " " + symbols [i].col() + " " + isWhite[i]);
			}
			writer.close(); // Garante que os dados são gravados 
			}
		catch (FileNotFoundException e)  {
			System.err.println("Erro a escrever no ficheiro.");
		}
	}

	void Random() {
	    // Arrays para armazenar capturas e movimentos
	    Position[] piecesForCaptures = new Position[symbols.length]; // Vetor que armazena as peças que podem capturar
	    Position[] possibleCaptures = new Position[symbols.length]; //  Vetor que irá armazenar o destino para onde essa peça vai 
	    int captureCount = 0; // Conta quantas capturas são possíveis num determinado momento 

	    Position[] piecesForMoves = new Position[symbols.length]; // Vetor que armazena as peças que se podem movdr
	    Position[] possibleMoves = new Position[symbols.length]; // Para onde se vão mover 
	    int moveCount = 0; // Conta quantos movimentos são possiveis naquele momento 

	    // Vai varrer o vetor symbols[i]
	    for (int i = 0; i < used; i++) {
	        Position from = symbols[i]; // Verifica a peça atual 

	        // Verifica se é a vez da peça (branca ou preta)
	        if ((WhiteTurn && isWhite[i]) || (!WhiteTurn && !isWhite[i])) {
	            // Cria os lugares possiveis para onda peça from pode ir se capturar
	            Position[] captureOptions = {
	                new Position(from.line() - 2, from.col() + 2), // Cima-direita
	                new Position(from.line() - 2, from.col() - 2), // Cima-esquerda
	                new Position(from.line() + 2, from.col() + 2), // Baixo-direita
	                new Position(from.line() + 2, from.col() - 2)  // Baixo-esquerda
	            };
	            
	            // Este for vai varrer esse vetor que contém os destinos possíveis se capturar

	            for (int j = 0; j < captureOptions.length; j++) {
	                Position to = captureOptions[j]; // vai associar a variavel "to" a cada possibilidade, uma de cada vex 
	                if (isValideEat(from, to)) { // Valida se é possível comer, se é avança 
	                    piecesForCaptures[captureCount] = from; // Coloca o from no vetor de peças que podem capturar 
	                    possibleCaptures[captureCount] = to; // E o to, para onde ela irá se captura
	                    captureCount++; // E diz que mais ou não uma captura possível naquela altura 
	                }
	            }

	            // Se não há capturas naquele momento, ele verifica movimentos normais
	            if (captureCount == 0) {
	                Position[] moveOptions = { // Cria um vetor com todos os movimentos possíveis para a peça from 
	                    new Position(from.line() - 1, from.col() + 1), // Cima-direita
	                    new Position(from.line() - 1, from.col() - 1), // Cima-esquerda
	                    new Position(from.line() + 1, from.col() + 1), // Baixo-direita
	                    new Position(from.line() + 1, from.col() - 1)  // Baixo-esquerda
	                };

	                for (int j = 0; j < moveOptions.length; j++) { // Varre esse vetor 
	                    Position to = moveOptions[j]; // E associa o to ao destino de um determinado movimento 
	                    if (isValidMove(from, to)) { // Se é possível mover, ele avança 
	                        piecesForMoves[moveCount] = from; // Coloca peça from no vetor de peças que podem-se mover 
	                        possibleMoves[moveCount] = to; // E para onde irá no possible moves 
	                        moveCount++; // E aumenta o nº de possíveis movimentos 
	                    }
	                }
	            }
	        }
	    }

	    // Realiza uma jogada aleatória
	    if (captureCount > 0) { // Se houver capturas possíveis, ele irá fazer 
	        int randomIndex = (int) (Math.random() * captureCount);
	        ToEat(piecesForCaptures[randomIndex], possibleCaptures[randomIndex]);
	    } else if (moveCount > 0) { // Se não houver, ele escolhe uma ao calhas para movimentar 
	        int randomIndex = (int) (Math.random() * moveCount);
	        play(piecesForMoves[randomIndex], possibleMoves[randomIndex]);
	    }
	}

}
