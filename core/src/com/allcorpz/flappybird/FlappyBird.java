package com.allcorpz.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.omg.PortableInterceptor.Interceptor;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] porcos;
	private Texture fundo, canoBaixo, canoTopo, canoBaixoMaior, canoTopoMaior, gameOver;
	private Random numeroRandomico;
	private BitmapFont Fonte, mensagem;
	private Circle porcoCircle;
	private Rectangle canoTopoRect, canoBaixoRect;
	private ShapeRenderer shape;

    // os 2 atrib. abaixo servem para ceder tamanho ao Sprite de fundo. (Olhe o metodo render)
    private float larguraDispositivo, alturaDispositivo;
	// o atrib. abaixo indica se o jogo iniciou: 0-> Não iniciado 1-> Iniciado 2->GameOver
    private int estadoJogo = 0, pontuacao = 0;

	//Atributos de configuração
	private float variacao = 0, velocidadeQueda = 0, posicaoInicialVertical, posicaoMovimentoCanoHorizontal;
	private float espacoEntreCanos, deltaTime, alturaEntreCanosRandomicos;
	private boolean marcouPonto = false;

	//Câmera
	private OrthographicCamera camera;
	private Viewport viewPort;
	private final float VIRTUAL_WIDTH = 768;
	private final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {
		batch = new SpriteBatch();
		numeroRandomico = new Random();

		//Criando CIRCLE e RECTANGLE do cano e do porco
		porcoCircle = new Circle();
		canoTopoRect = new Rectangle();
		canoBaixoRect = new Rectangle();

		shape = new ShapeRenderer();
		Fonte = new BitmapFont();
		Fonte.setColor(Color.BLACK);
		Fonte.getData().setScale(7);

		mensagem = new BitmapFont();
		mensagem.setColor(Color.BLACK);
		mensagem.getData().setScale(5);

		//Por conta do porco ter 3 sprites, ele se torna um vetor
		porcos = new Texture[3];
		porcos[0] = new Texture("passaro1.png");
		porcos[1] = new Texture("passaro2.png");
		porcos[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");

		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");
		canoBaixoMaior = new Texture("cano_baixo_maior.png");
		canoTopoMaior = new Texture ("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");

		/****************************************************
			CONFIGURAÇÕES DA CÂMERA - VIEWPORT
		 */

		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2, 0);
		viewPort = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo  = VIRTUAL_HEIGHT;

		posicaoInicialVertical  = alturaDispositivo/2;
		posicaoMovimentoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 300;

	}

	@Override
	public void render () {
		camera.update();

		// Limpar frames anteriores
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		deltaTime = Gdx.graphics.getDeltaTime();
		variacao += deltaTime * 10;
		//A linha abaixo permite a troca dos sprites do porco
		if (variacao > 2) variacao = 0;

		// Se o jogo não estiver iniciado
		if(estadoJogo == 0){
			if (Gdx.input.justTouched()) {
				estadoJogo = 1;
			}
		} else { //Iniciado
			// A linha abaixo regula a velocidade dos canos aproximando do porco

			velocidadeQueda++;
			if (posicaoInicialVertical > 0 || velocidadeQueda < 0)
				posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;

			if(estadoJogo == 1){

				posicaoMovimentoCanoHorizontal -= deltaTime * 350;

				if (Gdx.input.justTouched()) {
					velocidadeQueda = -15;
				}

				// Verifica se o cano saiu inteiramente da tela antes de dar respawn
				if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
					//Vai gerar números inteiros positivos de 0 a 400
					alturaEntreCanosRandomicos = numeroRandomico.nextInt(400) - 200;
					marcouPonto = false;
				}

				//Verifica Pontuação
				if(posicaoMovimentoCanoHorizontal < 120){
					if(!marcouPonto){
						pontuacao++;
						marcouPonto = true;
					}
				}

			} else { //Tela de gameover
				if (Gdx.input.justTouched()){
					estadoJogo = 0;
					pontuacao = 0;
					velocidadeQueda = 0;
					posicaoInicialVertical = alturaDispositivo / 2;
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
				}
			}

		}

		//Configurar dados de projeção da câmera
		batch.setProjectionMatrix(camera.combined);

			batch.begin();
			//bactch.draw(o sprite a ser colocado, X e Y como localizações, largura e altura do objeto)
			batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
			batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomicos);
			batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomicos);
			batch.draw(porcos[(int) variacao], 120, posicaoInicialVertical, 100, 90);
			Fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo/2, alturaDispositivo - alturaDispositivo/3);

			if (estadoJogo == 2){
				batch.draw(gameOver, larguraDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo/2);
				mensagem.draw(batch,"Pontos:" + String.valueOf(pontuacao) ,larguraDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo/2-100);
				mensagem.draw(batch,"RETRY" ,larguraDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo/2 - 300);
			}

			batch.end();

			porcoCircle.set(120 + porcos[(int) variacao].getWidth()/2,posicaoInicialVertical + porcos[0].getHeight()/2, porcos[(int) variacao].getWidth()/2 );
			canoBaixoRect = new Rectangle(
					//Recebe como parametro a posição X e Y e tamanho de dimensões
				posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomicos,
				canoBaixo.getWidth(), canoBaixo.getHeight()
			);
			canoTopoRect = new Rectangle(
					posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomicos,
					canoTopo.getWidth(), canoTopo.getHeight()
			);

			/*Desenhando formas
			shape.begin(ShapeRenderer.ShapeType.Filled);
			shape.circle(porcoCircle.x,porcoCircle.y,porcoCircle.radius);
			shape.end();*/

			//Teste de colisão
			if(Intersector.overlaps(porcoCircle, canoBaixoRect) || Intersector.overlaps(porcoCircle, canoTopoRect)
					|| posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo) {
				estadoJogo = 2;
			}
	}

	@Override
	public void resize(int width, int height) {
		viewPort.update(width, height);
	}
}
