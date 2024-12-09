import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import pt.iscte.guitoo.Color;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;

public class View {
	Damas model;
	Board board;
	Position selectedPosition;
	
	// Iniciar o jogo com um tabuleiro "pré-definido" 8x8 com 12 peças para cada jogar

	View(Damas model) {
		this.model = model; 
		initialboard(model.size(), model.pieces());
		
		
	}
	
 // Procedimento para iniciar o jogo conforme os dados que utilizador 
	
	void initialboard(int size, int piecesperplayer) {
		 	board = new Board("Jogo das damas", size, size, 75);  // Tamanho 8x8
	        board.setIconProvider(this::icon);
	        board.addMouseListener(this::click);
	        board.setBackgroundProvider(this::background);
	        board.addAction("Novo Jogo", this::newgame);
	        board.addAction("Salvar jogo", this:: savegame);
	        board.addAction("Carregar o jogo", this:: loadgame);
	        board.addAction("Aleatório", this:: random);
	        
	       // model.newgame(size, piecesperplayer); // Configura o modelo de jogo com as peças e tabuleiro padrão
	        updateTitle();
	        board.open();
	}
	
	// Procedimento que faz as perguntas e inicia um novo jogo c/ as dimensões que o utilizador quiser 
	
	void newgame() {
		int size = board.promptInt("Qual é o tamanho que deseja para o tabuleiro? ");
		
		while (size < 4) {
			size = board.promptInt("Qual é o tamanho que deseja para o tabuleiro? ");
		}
		
		int piecesperplayer = board.promptInt("Quantas peças deseja para cada jogador? ");
		
		while (piecesperplayer < 1) {
			piecesperplayer = board.promptInt("Quantas peças deseja para cada jogador? ");
		}
		Damas newModel = new Damas(size, piecesperplayer);
		//newModel.newgame(size, piecesperplayer);
		//model = newModel;
		//initialboard(size, piecesperplayer);
	    View view = new View(newModel);
	    view.start();
	}
	
	// Procedimento para salvar uma partida 
	void savegame() {
		String filename = board.promptText("Digie o nome do ficheiro em que quer salvar o jogo:");
		if ( filename != null) {
			model.save(filename);
			board.showMessage("O jogo foi salvo c/ sucesso no ficheiro:" + filename);
		}
	}
	
	// Procedimento para ler um ficheiro com uma partida previamente gravada
	void loadgame() {
			try {
				String filename = board.promptText("Digite o nome do ficheiro que quer carregar:");
				
				int size1 = 0; 
				boolean WhiteTurn1 = true;
				int movecount = 0;
				Scanner scanner = new Scanner (new File(filename));
				Position [] symbols1 = new Position[scanner.nextInt()];
				boolean [] isWhite1 = new boolean [symbols1.length];
				size1 = scanner.nextInt();
				WhiteTurn1 = scanner.nextBoolean();
				movecount = scanner.nextInt();
				
				 
				int line = 0; 
				int col = 0;
				for ( int i = 0; i < symbols1.length; i++) {
					line = scanner.nextInt();
					col = scanner.nextInt();
					Position piece1 = new Position (line, col);
					symbols1 [i] = piece1;
					isWhite1 [i] = scanner.nextBoolean();			
				}
				scanner.close();
				
				Damas damas = new Damas(size1, symbols1, isWhite1,WhiteTurn1, movecount);
				View gui = new View(damas);
				gui.start();

			}
			catch (FileNotFoundException e) {
				System.err.println("Erro a carregar o ficheiro");
			}
			
		}
	
	// Botão do random 
	
	void random() {
		if (model.ItsOver()) {
			return;
		}
		else {
			model.Random();
			updateTitle();
		}
	}
	
// Função para dizer "o icon" das peças 
	String icon(int line, int col) {
		Position p = new Position(line, col);
		if (model.isVisited(p)) {
			return model.isWhite(p) ? "white.png" : "black.png";
		}
		return null;
	}
	
	
	// O que faz o jogo "funcionar", o clique 
	void click(int line, int col) {
	    Position clickPosition = new Position(line, col);

	    if (model.ItsOver()) {
	        selectedPosition = null;
	    } else {

	        if (selectedPosition == null) {
	            // Selecionar uma peça verificando qual é o turno 
	            if (model.isVisited(clickPosition) &&
	                ((model.isWhite(clickPosition) && model.isWhiteTurn()) ||
	                 (!model.isWhite(clickPosition) && !model.isWhiteTurn()))) {
	                selectedPosition = clickPosition; // Coloca a peça clicada como "selecionada"
	            }
	        } else {
	            // Se uma peça já foi selecionada, verifica se há uma captura disponível
	            if (model.CaptureAvailable()) { // Se há capturas disponíveis
	                if (model.isValideEat(selectedPosition, clickPosition)) {
	                    // Realiza a captura se for válida
	                    model.ToEat(selectedPosition, clickPosition);
	                    selectedPosition = null; // Reseta a seleção
	                    updateTitle(); // Atualiza o título após a captura
	                } else {
	                    selectedPosition = null; // Desseleciona a peça se a captura for inválida
	                }
	            } else if (model.isEmpty(clickPosition) && model.isValidMove(selectedPosition, clickPosition)) {
	                // Se não houver nada para comer, realiza o movimento normal
	                model.play(selectedPosition, clickPosition);
	                selectedPosition = null; // Reseta a seleção
	                updateTitle(); // Atualiza o título após o movimento
	            } else {
	                // Reset para ações inválidas
	                selectedPosition = null;
	            }
	        }
	    }
	}

	
	void updateTitle() {
	    if (model.ItsOver()) {
	        int whitePieces = model.CountWhitePieces();
	        int blackPieces = model.CountBlackPieces();

	        if (whitePieces == 0) {
	            board.setTitle("O jogo terminou! As peças pretas ganharam.");
	        } else if (blackPieces == 0) {
	            board.setTitle("O jogo terminou! As peças brancas ganharam.");
	        } else if (model.isWhiteTurn() && whitePieces < blackPieces) {
	            board.setTitle("O jogo terminou! As peças pretas venceram (brancas sem movimentos).");
	        } else if (!model.isWhiteTurn() && blackPieces < whitePieces) {
	            board.setTitle("O jogo terminou! As peças brancas venceram (pretas sem movimentos).");
	        }
	        else if ((!model.isWhiteTurn() || model.isWhiteTurn()) && blackPieces == whitePieces){
	        	board.setTitle("O jogo terminou! Houve empate");
	        }
	    } else {
	        int moveCount = model.getMoveCOunt();
	        int whitePieces = model.CountWhitePieces();
	        int blackPieces = model.CountBlackPieces();
	        String title = model.isWhiteTurn()
	            ? "Jogada nº: " + moveCount + " | Brancas jogam | Peças brancas: " + whitePieces
	            : "Jogada nº: " + moveCount + " | Pretas jogam | Peças pretas: " + blackPieces;
	        board.setTitle(title);
	    }
	}
	
	Color background(int line, int col) {
		Position p = new Position(line, col);
		if (selectedPosition != null && selectedPosition.equals(p)) {
			return StandardColor.YELLOW;
		}
		if ((p.line() + p.col()) % 2 == 0)
			return StandardColor.WHITE;
		else
			return StandardColor.BLACK;
	}

	void start() {
		board.open();
	}

	public static void main(String[] args) {
		Damas damas = new Damas();
		View gui = new View(damas);
		gui.start();
	}
}