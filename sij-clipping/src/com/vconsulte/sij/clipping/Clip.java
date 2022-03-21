package com.vconsulte.sij.clipping;

/*
 * 	versao 2.4.08	- 28 de Novembro 2020
					- Camada do método atualizaEdicoes após loop de edicoes
					- Ajustes nas linhas de log
					- 
	versao 2.6		- 10 de Abril de 2021
					- Equalização na utilização do método Comuns.apresentamensagem
					- 
	versao 2.7		- XX de Junho de 2021
					- Processamento sem necessidade de parametros
					- envio do relatorio para o servidor
*/


	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileWriter;
	import java.io.IOException;
	//import java.io.PrintWriter;
	import java.io.UnsupportedEncodingException;
	//import java.sql.ResultSet;
	import java.text.SimpleDateFormat;
	import java.util.ArrayList;
	import java.util.Calendar;
	import java.util.Date;
	//import java.util.GregorianCalendar;
	//import java.util.Iterator;
	import java.util.List;
	//import java.util.Map;
	//import java.util.Set;
	//import java.util.Date;
	import java.text.DateFormat;
	//import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.apache.chemistry.opencmis.client.api.Folder;
	import org.apache.chemistry.opencmis.client.api.Session;
	import com.vconsulte.sij.base.InterfaceServidor;
	import com.vconsulte.sij.base.GravaTexto;
	import com.vconsulte.sij.base.Comuns;

public class Clip {

	static InterfaceServidor conexao = new InterfaceServidor();
	static Session sessao;
	static File config;
	
	static List <String> idEdicoes = new ArrayList<String>();
	static List <String> log = new ArrayList<String>();
	
	static String [][] tabClientes = new String[100][2];
	static String [] parametros = null;
	
	static String cliente;
	static String usuario;
	static String url;
	static String password;
	static String pastaCarregamento;
	static String pastaPublicacoes;
	static String pastaTokens;
	static String pastaSaida;
	static String pastaLog;
	static String pastaRelatorios;
	static String tipoDocumento;
	static String edtFolderName = "";
	static String relatorioDeClipping = "";
	static String versaoClipping = "";

	static String idToken;
	
	static String[] listaEdicoes = new String[55];
	static String[] listData = new String[55];
	static String publicacoesLocalizadas[][] = new String [5000][2];

	static String edicaoEscolhida = "";	
	static String token = "";
    static String a = null;
    static String newline = "\n";
    static String caminho = "";
    static String tribunal = "todos";

	static int k =0;
	static int opcao;
	static int limiteClientes;
	
	static boolean escolheu = false;
	static boolean parametrizado = false;
	final static String tipoProcessamento = "BATCH";

	public static void main(String[] args) throws Exception {
		String dummy = "";
		if(args.length>0) {
			dummy = args[0];
		    parametros = dummy.split(",");
		    parametrizado = true;
		}
		localizaPublicacoes();
	}
	
	private static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

    @SuppressWarnings("static-access")
	private static void localizaPublicacoes() throws Exception, IOException, Exception {   	
    	int qtdPublicacoes = 0;
    	String dummy = "";
    	String dtEdicao = "";
//    	List <String> edicoesPorTribunal = new ArrayList<String>();
    	List <String> edicoesNaoClipadas = new ArrayList<String>(); 
    	registraLog("Início do processamento do Clippingb\n");
    	registraLog("Carregando tabelas");
    	com.vconsulte.sij.base.Parametros.carregaTabelas();
    	registraLog("Carregando configuração");
    	com.vconsulte.sij.base.Configuracao.carregaConfig();

    	cliente = com.vconsulte.sij.base.Parametros.CLIENTE;
		url = com.vconsulte.sij.base.Parametros.URL;
		usuario = com.vconsulte.sij.base.Parametros.USUARIO;
		password = com.vconsulte.sij.base.Parametros.PASSWORD;
		limiteClientes = com.vconsulte.sij.base.Parametros.LIMITECLIENTES;
		pastaCarregamento = com.vconsulte.sij.base.Parametros.PASTACARREGAMENTO;
		pastaPublicacoes = com.vconsulte.sij.base.Parametros.PASTAPUBLICACOES;
		pastaSaida = com.vconsulte.sij.base.Parametros.PASTASAIDA;
		pastaTokens = com.vconsulte.sij.base.Parametros.PASTATOKENS;
		tipoDocumento = com.vconsulte.sij.base.Parametros.TIPODOCUMENTO;
		pastaLog = com.vconsulte.sij.base.Parametros.LOGFOLDER;
		pastaRelatorios = com.vconsulte.sij.base.Parametros.PASTARELATORIOS;
		versaoClipping = com.vconsulte.sij.base.Parametros.VERSAOCLIPPING;
		String [][] tabClientes = new String[limiteClientes][2];
		
		System.out.print("\n");
		Comuns.apresentaMenssagem("-----------------------------------------------------------------------------", tipoProcessamento, "informativa", null);
		Comuns.apresentaMenssagem("Clipping " + versaoClipping + " - Seleção de Publicações.", tipoProcessamento,"informativa",  null);
		Comuns.apresentaMenssagem("-----------------------------------------------------------------------------", tipoProcessamento, "informativa", null);
    	System.out.print("\n");
    	Comuns.apresentaMenssagem("-----------------------------------------------------------------", tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("Parâmetros de processamento:", tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("\tServidor: " + url, tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("\tPasta Carregamnto: " + pastaCarregamento, tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("\tPasta Publicações: " +pastaPublicacoes, tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("\tPasta Saida: " +pastaSaida, tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("\tPasta Tokens: " +pastaTokens, tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("\tPasta de Logs: " +pastaLog, tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("\tPasta de Relatorios: " + pastaRelatorios, tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("-----------------------------------------------------------------", tipoProcessamento,"informativa", null);
    	System.out.print("\n");
    	
    	registraLog("Conectando servidor.");
    	Comuns.apresentaMenssagem("Conectando com o servidor.",tipoProcessamento,"informativa", null);
		sessao = Comuns.conectaServidor(usuario, password, url);
        Comuns.apresentaMenssagem("Servidor conectado com sucesso.",tipoProcessamento,"informativa", null);
		if (sessao == null) {
			registraLog("Falha na conexção com o servidor, processamento encerrado.");
    		Comuns.apresentaMenssagem("Falha na conexão com o servidor",tipoProcessamento,"erro", null);
			Comuns.finalizaProcesso(tipoProcessamento);
		}
		
		/*
		 * Usado para o caso de necessidade de reverter o processamento do clipping
		 * 
		 * reverterEdicoes("26-02-2021");	-> informe a data da edição a ser revertida
		 * 
		 */

//		reverterEdicoes("27-09-2021");			// não apague
		
//		if(parametros.length>0 && !parametros[0].equals("all")) {
			for(int ix=0; ix<=parametros.length-1;ix++) {
				tribunal = parametros[ix];
//				edicoesPorTribunal.clear();
				registraLog("Carregando tabela de clientes");
				tabClientes = conexao.listarClientes(sessao, limiteClientes);
				for(int ix1=0; ix1<=tabClientes.length-1; ix1++) {
					if(tabClientes[ix1][0] != null) {
						if(!parametros[0].equals("all") && !parametros[0].equals("servidor")) {
							edicoesNaoClipadas = conexao.listarEdicoesPorTribunal(sessao, tribunal);
						} else {
							edicoesNaoClipadas = conexao.listaEdicoesNaoClipadas(sessao);
						}
						
						if(edicoesNaoClipadas.isEmpty()) {
							if(!parametros[0].equals("all") && !parametros[0].equals("servidor")) {
								registraLog("Não há edições para o tribunal: " + parametros[ix]);
					    		Comuns.apresentaMenssagem("Não há edições para o tribunal: " + parametros[ix],tipoProcessamento,"erro", null);
					    		relatorioDeClipping = relatorioDeClipping + "Não edições para o tribunal: " + parametros[ix];
							} else {
								registraLog("Não há edições para processar");
					    		Comuns.apresentaMenssagem("Não há edições para processar",tipoProcessamento,"erro", null);
					    		relatorioDeClipping = relatorioDeClipping + "Não há edições para processar";
							}
							
						} else {
							for(int ix2=0; ix2<=edicoesNaoClipadas.size()-1;ix2++) {
								if(parametros[0].equals("all") || parametros[0].equals("servidor")) {
	//								qtdPublicacoes = conexao.verificaQtdPublicacoes(sessao, edicoesNaoClipadas.get(ix2));			
									tribunal = conexao.obtemTribunalDaEdicao(sessao, edicoesNaoClipadas.get(ix2));
								}
								if(indexar(edicoesNaoClipadas.get(ix2), tabClientes[ix1][0], tabClientes[ix1][1])){
									InterfaceServidor.atualizaEdicaoClipada(sessao, tabClientes[ix1][0], edicoesNaoClipadas.get(ix2));	
								}
								dtEdicao =  conexao.obtemEdicaoPasta(sessao, edicoesNaoClipadas.get(ix2));
								dtEdicao = dtEdicao.replace("/", "-");
								String nomeRelatorio = pastaLog + "/clp-"+ tribunal + "-" + dtEdicao + ".txt";
								registraLog("Gravando relatorio de indexação.");
								registraLog("Fim do Processamento.");							
								gravaArquivo(nomeRelatorio, relatorioDeClipping); 				//String nomeArquivo, String linhas
								enviaRelatorio("clp-"+ tribunal + "-" + dtEdicao + ".txt", relatorioDeClipping);								
							}
						}
					}
					break;
				}
			}
//		}
		System.out.println("\n");
    	Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("Fim do processamento do Clipping.",tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
		registraLog("Fim do processamento do Clipping.");
    	dummy = getDateTime().replace("/", "-");
		dummy = dummy.replace(":", "-");
		dummy = dummy.replace(" ", "_");
		String logName = pastaLog + "/" + tribunal + "-indexacao-" + dummy + ".log";
		gravaLog(log, logName);
		log.clear();
    }
    
    private static boolean indexar(String edicao, String cliente, String destino) throws NullPointerException, IOException, Exception {
    	int ix = 0;
    	int publicacoesIncluidas = 0;
    	int localizados = 0;
    	int naoLocalizados = 0;
    	int totalPublicacoes = 0;
    	int totalTokens = 0;
    	int dupl = 0;
    	int qtdPublicacoes = 0;
    	boolean edicaoRegistrada = false;
    	String linha = "";  	
    	String [][] edicoesIndexadas = new String [1000][2];
    	String dummy = "";
    	List <String> tabelasTokens = new ArrayList<String>();
    	List <String> tokens = new ArrayList<String>();   	
    	List <String> publicacoes = new ArrayList<String>();
    	List <String> edicoesSelecionadas = new ArrayList<String>();
    	registraLog("Início da Indexação para: " + cliente + "/" + tribunal);
    	
    	registraLog("Obtendo tabela de tokens para o cliente: " + tribunal + "/" + cliente);
    	tabelasTokens = conexao.obtemTabelasTokens(sessao, cliente, tribunal);
    	
    	if(!tabelasTokens.isEmpty()) {
    		if(tabelasTokens.size() == 1) {
    			registraLog("Início da localização das publicações para o tribunal: " + tribunal);
    			Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
        		Comuns.apresentaMenssagem("Início da localização das publicações para o tribunal: " +tribunal, tipoProcessamento,"informativa", null);
        		relatorioDeClipping = getDateTime() + ">>> Início do processamento do tribunal: " + cliente + "/" + tribunal + " <<<\n";
        		relatorioDeClipping = relatorioDeClipping + "-----------------------------------------------------------------\n";
        		registraLog(">>> Início do processamento do tribunal: " + cliente + "/" + tribunal + " <<<");
        		localizados = 0;
        		naoLocalizados = 0;
        		publicacoesIncluidas = 0;
        		totalTokens = 0;

    			registraLog("Localizada(s) " + edicoesSelecionadas.size() + " edições não indexada(s) para o tribunal " + cliente + "/" + tribunal);
        		Comuns.apresentaMenssagem("Localizada(s) edições " + edicoesSelecionadas.size() + " não indexada(s) para o tribunal " + cliente + "/" + tribunal, tipoProcessamento,"informativa", null);
        		
        		registraLog("Início do loop de tokens para o tribunal: " + cliente + "/" + tribunal);
        		Comuns.apresentaMenssagem("Início do loop de tokens para o tribunal: " + cliente + "/" + tribunal, tipoProcessamento,"informativa", null);
        		relatorioDeClipping = relatorioDeClipping + "\n" + Comuns.obtemHrAtual() + " - Localizando publicações para o tribunal: " + cliente + "/" + tribunal + " -> " + edicoesSelecionadas.size()+"\n";     
	        		
    			registraLog("Verificando se a edição: " + edicao + " já foi verificada para o tribunal " + cliente + "/" + tribunal);
    			Comuns.apresentaMenssagem("Verificando se a edição já foi verificada. ", tipoProcessamento,"informativa", null);
    			if(conexao.verificaEdicaoNaoClipada(sessao, edicao, cliente)) {
    				registraLog("A edição: " + edicao + "  ainda não foi verificada. " + cliente + "/" + tribunal);
    				Comuns.apresentaMenssagem("A edição ainda não foi verificada.", tipoProcessamento,"informativa", null);
    				edicoesIndexadas[ix][0] = cliente;
		        	edicoesIndexadas[ix][1] = edicao;
    				ix++;
    				Comuns.apresentaMenssagem("Carregando tabela de tokens para o tribunal: " + cliente + "/" + tribunal, tipoProcessamento,"informativa", null);
    				registraLog("Carregando tabela de tokens para o tribunal: " + cliente + "/" + tribunal);
		        	tokens = conexao.carregaTokensBatch(sessao, tabelasTokens.get(0));
		        	if(!tokens.isEmpty()) {
		        		totalTokens = tokens.size();
			        	registraLog("Tabela de tokens carregada");
			        	Comuns.apresentaMenssagem("Carregados " + totalTokens + " tokens desta tabela de tokens do tribunal: " + cliente + "/" + tribunal, tipoProcessamento,"informativa", null);
			        	registraLog("Carregados " + totalTokens + " tokens desta tabela de tokens do tribunal: " + cliente + "/" + tribunal);
        				edicaoRegistrada = false;
        				for(int ix4=0; ix4<=tokens.size()-1; ix4++) {				// ix4 - loop de procura para cada token
        					//Comuns.apresentaMenssagem("Localizando publicações para o token:  " + tokens.get(ix4).trim(), tipoProcessamento,"informativa", null);
        					registraLog("Localizando publicações para o token:  " + tokens.get(ix4).trim());      				
        					
        					
        					
        					
        					k++;
    //    					qtdPublicacoes = conexao.verificaQtdPublicacoes(sessao, edicao);
        					qtdPublicacoes = 1;

        					
        					
        					
        					if(qtdPublicacoes > 0){

        						publicacoes = conexao.localizaPublicacoes(sessao, edicao, tribunal, tokens.get(ix4).trim());

        						if(!publicacoes.isEmpty()) {
	        						registraLog("Localizados: " + publicacoes.size() + " publicações para o token: " + tokens.get(ix4).trim());
	        						relatorioDeClipping = relatorioDeClipping + "\n  Localizado(s):  " + publicacoes.size() + " publicações para o token: " + tokens.get(ix4).trim() + "\n\n"; 
	        						Comuns.apresentaMenssagem("Localizados: " + publicacoes.size() + " publicações para o token: " + tokens.get(ix4).trim(), tipoProcessamento,"informativa", null);
					        		localizados++;
	
					        		for(int ix5=0; ix5<=publicacoes.size()-1; ix5++) {	// registra publicacoes localizadas
			        					dupl = verificaDuplicidadeEditais(publicacoes.get(ix5));		        					
			        					if(dupl >= 0) {
			        						publicacoesLocalizadas[dupl][1] = publicacoesLocalizadas[dupl][1] + "\n" + tokens.get(ix4).trim();
			        						registraLog("*** Publicação atualizada: " + publicacoesLocalizadas[publicacoesIncluidas][0] + " - " + publicacoesLocalizadas[publicacoesIncluidas][1]+"\n");		
			        					} else {		        					
			        						publicacoesLocalizadas[publicacoesIncluidas][0] = publicacoes.get(ix5).trim();
			            					publicacoesLocalizadas[publicacoesIncluidas][1] = tokens.get(ix4).trim();		            					            					
			            					registraLog("+++ Publicação incluída: " + publicacoesLocalizadas[publicacoesIncluidas][0] + " - " + publicacoesLocalizadas[publicacoesIncluidas][1]+"\n"); 					
			            					publicacoesIncluidas++;
			        					}		        					
				        			}   
					        		k++;
	        					} else {
	        						relatorioDeClipping = relatorioDeClipping + "Nenhuma publicação localizada para o token: " + tokens.get(ix4).trim() + "\n";
			        				registraLog("Nenhuma publicação localizada para o token: " + tokens.get(ix4).trim());
			        				Comuns.apresentaMenssagem("Nenhuma publicação localizada para o token: " + tokens.get(ix4).trim(), tipoProcessamento,"informativa", null);
			        				naoLocalizados++;
	        					}
	        				} else {
	        					Comuns.apresentaMenssagem("Edição sem pubicações: Tribunal:" + tribunal + "\n", tipoProcessamento,"informativa", null);
	        					registraLog("Edição sem pubicações: Tribunal:" + tribunal);
	        					relatorioDeClipping = relatorioDeClipping + "Edição sem pubicações: Tribunal:" + tribunal + "\n";
	        					localizados = 0;
	        					break;
	        				}
        					
        					
        					
        					
        				}
        				if(localizados == 0) {
                			linha = ">>> " + cliente + "/" + tribunal + "\t\t *** Nenhuma publicação selecionada ***";
                			registraLog("\">>> \" + cliente + \"/\" + tokenTribunal + \"\\t\\t *** Nenhuma publicação selecionada ***\"");
                			relatorioDeClipping = relatorioDeClipping + "\n" + linha + "\n";   
                			Comuns.apresentaMenssagem(linha, tipoProcessamento,"informativa", null);
                		} else {
                			totalPublicacoes = totalPublicacoes + publicacoesIncluidas;
                			linha = ">>> " + cliente + "-" + tribunal + " - Total de tokens: " + totalTokens + " / Localizados: \t" + localizados + " / Não localizados: " + naoLocalizados + " / Publicações afetadas: " + publicacoesIncluidas;
	        				registraLog("\">>> \" + cliente + \"-\" + tokenTribunal + \" - Total de tokens: \" + totalTokens + \" / Localizados: \\t\" + localizados + \" / Não localizados: \" + naoLocalizados + \" / Publicações afetadas: \" + publicacoesIncluidas");
	        				relatorioDeClipping = relatorioDeClipping + linha + "\n";      		   		
	                		Comuns.apresentaMenssagem(linha, tipoProcessamento,"informativa", null);
                			registraLog("Copiando publicações localizadas para o site do cliente.");
                			copiaPublicacoes(cliente, destino, tribunal);
                			dummy = getDateTime().replace(":", "-");
                			dummy = getDateTime().replace("/", "-");
                			relatorioDeClipping = relatorioDeClipping + "\n-----------------------------------------------------------------\n";
                			registraLog("Gravando relatorio de indexação para o tribunal: " + cliente + "/" + tribunal);
                			limparPublicacoesLocalizadas();					                			
                		}	
		        	} else {
		        		registraLog("Tabela de tokens estar vazia: " + cliente + "/" + tribunal);
    	        		Comuns.apresentaMenssagem("Tabela de tokens estar vazia: " + cliente + "/" + tribunal, tipoProcessamento,"informativa", null);
    	        		relatorioDeClipping = relatorioDeClipping + "\n" + Comuns.obtemHrAtual() + " - Tabela de tokens estar vazia: " + cliente + "/" + tribunal + " -> " + edicoesSelecionadas.size()+"\n";    
    	        		return false;
		        	}
    			} else {
    				registraLog("Edição já com clipping já processado: " + cliente + "/" + tribunal);
	        		Comuns.apresentaMenssagem("Edição já com clipping já processado: " + cliente + "/" + tribunal, tipoProcessamento,"informativa", null);
	        		relatorioDeClipping = relatorioDeClipping + "\n" + Comuns.obtemHrAtual() + " - Edição já com clipping já processado: " + cliente + "/" + tribunal + " -> " + edicoesSelecionadas.size()+"\n";    
    			}
    		} else {
    			registraLog("Só pode existir uma tabela de tokens para:" + cliente + "/" + tribunal);
        		Comuns.apresentaMenssagem("Só pode existir uma tabela de tokens para:" + cliente + "/" + tribunal, tipoProcessamento, "informativa", null);
        		relatorioDeClipping = relatorioDeClipping + "Só pode existir uma tabela de tokens para:" + cliente + "/" + tribunal;
        	}
    	} else {
    		registraLog("Não existe tabela de tokens para:" + cliente + "/" + tribunal);
    		Comuns.apresentaMenssagem("Não existe tabela de tokens para:" + cliente + "/" + tribunal, tipoProcessamento, "informativa", null);
    		relatorioDeClipping = "Não existe tabela de tokens para: " + cliente + "/" + tribunal;
    		return false;
    	}
    	return true;
    }
    
	private static void enviaRelatorio(String nomeArquivo, String linhas) throws Exception {
		List <String> texto = new ArrayList<String>();
		registraLog("Enviando relatorio ao servidor");
		texto.add(linhas);
		Date dataAtual = new Date();
		String relatoriosNoServidor = pastaRelatorios;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//        String nomeRelatorio = "spl-resumo-" + dateFormat.format(dataAtual) + ".txt";
        String nomePasta = dateFormat.format(dataAtual);
        Folder pastaRelatorios = InterfaceServidor.verificaPastaRelatorios(sessao, relatoriosNoServidor, nomePasta, nomeArquivo);
        conexao.enviaRelatorio(sessao, pastaRelatorios, nomeArquivo, texto);
	}
    
    private static void registraLog(String registroLog) {
		log.add(getDateTime() + " - " + registroLog);
	}
    
    private static String completaEsquerda(String value, char c, int size) {
		String result = value;
		while (result.length() < size) {
			result = c + result;
		}
		return result;
	}
    
    private static String obtemHrAtual() {

		String hr = "";
		String mn = "";
		String sg = "";
		Calendar data = Calendar.getInstance();
		hr = Integer.toString(data.get(Calendar.HOUR_OF_DAY));
		mn = Integer.toString(data.get(Calendar.MINUTE));
		sg = Integer.toString(data.get(Calendar.SECOND));
		return completaEsquerda(hr,'0',2)+":"+completaEsquerda(mn,'0',2)+":"+completaEsquerda(sg, '0', 2);
	}
    
    private static void gravaLog(List<String> log, String arquivoLog) throws IOException {
    	StringBuilder blocoTexto = new StringBuilder();
		String bufferSaida = "";
		blocoTexto.append(log);
		FileWriter arqSaida = new FileWriter(arquivoLog);
		BufferedWriter bw = new BufferedWriter(arqSaida);
		bufferSaida = blocoTexto.toString();
		bw.write(bufferSaida);
		bw.close();
    }
    
    private static void copiaPublicacoes(String cliente, String destino, String tribunal) throws UnsupportedEncodingException, Exception {
    	String pastaNome;
    	String descricao;
    	String strEdicao;
    	String nomePublicacao;
    	Folder idPastaDestino;
    	String idPubicacaoCopiada;
    	String queryString;
    	String linha = "";
    	Date edicao;
    	int dupl = 0;
    	int totalCopiadas = 0;
    	System.out.println("\n");
    	registraLog("Início da movimentação daNão há edicoes para este tribunals publicações localizadas");
    	Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
    	Comuns.apresentaMenssagem("Início da movimentação das publicações localizadas", tipoProcessamento,"informativa", null);
		for (int ix = 0; ix <= publicacoesLocalizadas.length; ix++) {

			if(publicacoesLocalizadas[ix][0] == null) {
				break;
			}

			nomePublicacao = obtemInformacaoPublicacao(publicacoesLocalizadas[ix][0],02);
			strEdicao = obtemInformacaoPublicacao(publicacoesLocalizadas[ix][0],04);
			pastaNome = obtemInformacaoPublicacao(publicacoesLocalizadas[ix][0],12);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        sdf.setLenient(false);
	        edicao = sdf.parse(strEdicao);
	        strEdicao = strEdicao.substring(8, 10) + "-" + strEdicao.substring(5, 7) + "-" + strEdicao.substring(0, 4);
	        edtFolderName = pastaNome;
	        descricao = "Publicações localizadas para o TRT: " + tribunal + "ª Região";
			idPastaDestino = InterfaceServidor.verificaPastaPublicacao(sessao,  destino, pastaNome+"X", descricao, tribunal, edicao, cliente, strEdicao);

			if(!InterfaceServidor.copiaPublicacao(sessao, publicacoesLocalizadas[ix][0], pastaCarregamento+"/"+pastaNome, destino+"/"+pastaNome+"X", nomePublicacao, cliente)) {
			//	Comuns.apresentaMenssagem("Publicação copiada -->" + ix + "-" + publicacoesLocalizadas[ix][0], tipoProcessamento,"informativa", null);
			//	relatorioDeClipping = relatorioDeClipping +  "Publicação copiada -->" + ix + "-" + publicacoesLocalizadas[ix][0] + "\n";
				registraLog("Publicação copiada -->" + ix + "-" + publicacoesLocalizadas[ix][0]);
				dupl = verificaDuplicidadeEditais(publicacoesLocalizadas[ix][0]);
			}

			queryString = "SELECT cmis:objectId FROM cmis:document WHERE in_folder('" + idPastaDestino + "') AND cmis:name='" + nomePublicacao + "' AND cmis:lastModifiedBy='sij'";
			queryString = trataQueryString(queryString);
			idPubicacaoCopiada = conexao.getFileId(sessao, queryString);
						
//			idPubicacaoCopiada = InterfaceServidor.getFileId(sessao, idPastaDestino, nomePublicacao);

			descricao = "Publicação localizada - TRT: " + tribunal + "ª Região - edição: "  + strEdicao + "\n\n" +
						" **** Tokens Localizados ****" + "\n" + publicacoesLocalizadas[ix][1];
			registraLog(cliente + " - Publicação incluída na site do cliente: " + cliente + " - " + nomePublicacao);
			//relatorioDeClipping = relatorioDeClipping + " - Publicações incluída na site do cliente: " + cliente + " - " + nomePublicacao + "\n";
			Comuns.apresentaMenssagem(" - Publicação incluída no site do cliente: " + cliente + " - " + nomePublicacao, tipoProcessamento,"informativa", null);			
			InterfaceServidor.atualizaPublicacaoClipada(sessao, idPubicacaoCopiada, descricao, cliente, publicacoesLocalizadas[ix][1]);
		}
		registraLog("Fim da movimentação das publicações localizadas: " + cliente + "/" + tribunal);
		Comuns.apresentaMenssagem("Fim da movimentação das publicações localizadas para o cliente: " + cliente + "/ Tribunal: " + tribunal, tipoProcessamento,"informativa", null);
		Comuns.apresentaMenssagem("-----------------------------------------------------------------",tipoProcessamento,"informativa", null);
    }
    
    private static String obtemInformacaoPublicacao(String idPublicacao, int indice) {
    	List <String> infoPublicacao = new ArrayList<String>();
    	infoPublicacao = InterfaceServidor.obtemInformacosPublicacao(sessao, idPublicacao);
		return infoPublicacao.get(indice);
    }
    
    private static String trataQueryString(String query) {
    	String queryTratada = "";
    	String dummy = "";
    	String [] arrayLinha = query.split(" ");
    	for(int ix=0; ix<= arrayLinha.length-1; ix++) {
    		if(arrayLinha[ix].equals("in_folder('CMIS_FOLDER") || arrayLinha[ix].equals("(cmis:folder):")) {
    			continue;
    		} else {
    			dummy = dummy + " " + arrayLinha[ix];
    		}    		
    	}
    	String [] arrayFinal = dummy.trim().split(" ");
    	for(int ix=0; ix<= arrayFinal.length-1; ix++) {
    		if(arrayFinal[ix].equals("WHERE")) {
    			queryTratada = queryTratada + " " + arrayFinal[ix] + " in_folder('" + arrayFinal[ix+1].trim();
    			ix++;
    		} else {
    			queryTratada = queryTratada + " " + arrayFinal[ix].trim();
    		}
    	}	
    	return queryTratada.trim();
    }
    
    private static int verificaDuplicidadeEditais(String idEdital) {
    	for(int ix=0; ix<=publicacoesLocalizadas.length-1; ix++) {
    		if(publicacoesLocalizadas[ix][0] != null) {
    			if(publicacoesLocalizadas[ix][0].equals(idEdital)) {
	    			return ix;
	    		}
    		} else {
    			break;
    		}	    		
    	}
    	return -1;
    }
 
    private static void limparPublicacoesLocalizadas() {
    	for(int ix=0; ix<=publicacoesLocalizadas.length-1; ix++) {
    		publicacoesLocalizadas[ix][0] = null;
    		publicacoesLocalizadas[ix][1] = null;
    	}
    }
    
    private static void gravaArquivo(String nomeArquivo, String linhas) throws IOException {
		String bufferSaida = "";
		StringBuilder bloco = new StringBuilder();
		bloco.append(linhas);
		FileWriter arqSaida = new FileWriter(nomeArquivo);
		BufferedWriter bw = new BufferedWriter(arqSaida);
		bufferSaida = bloco.toString();
		bw.write(bufferSaida);
		bw.close();
	}
    
    private static void reverterEdicoes(String edicao) throws ParseException {
    	
    	/*
    	 * Para casos de ser necessário reverter o processamento do clipping
    	 */
    	
    	List <String> edicoesParaReverter = new ArrayList<String>();
    	edicoesParaReverter = conexao.listarEdicoesPorEdicao(sessao, edicao);
    	if(edicoesParaReverter.size()>0) {
    		for(int ix=0; ix<= edicoesParaReverter.size()-1; ix++) {
    			conexao.reverteEdicaoProcessada(sessao, edicoesParaReverter.get(ix));
    		}	
    	}  
    	k++;
    }
}
