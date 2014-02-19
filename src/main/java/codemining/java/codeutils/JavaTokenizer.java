package codemining.java.codeutils;

import java.util.List;
import java.util.SortedMap;
import java.util.logging.Logger;

import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.core.util.PublicScanner;

import codemining.languagetools.ITokenizer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A Java Code tokenizer using Eclipse JDT.
 * 
 * @author Miltos Allamanis <m.allamanis@ed.ac.uk>
 * 
 */
public class JavaTokenizer implements ITokenizer {

	private static final long serialVersionUID = 505587999946057082L;
	private static final Logger LOGGER = Logger.getLogger(JavaTokenizer.class
			.getName());

	/**
	 * A filter for the files being tokenized.
	 */
	public static final RegexFileFilter javaCodeFileFilter = new RegexFileFilter(
			".*\\.java$");

	private final boolean tokenizeComments;

	public static final String IDENTIFIER_ID = Integer
			.toString(ITerminalSymbols.TokenNameIdentifier);

	public JavaTokenizer() {
		tokenizeComments = false;
	}

	public JavaTokenizer(final boolean tokenizeComments) {
		this.tokenizeComments = tokenizeComments;
	}

	@Override
	public SortedMap<Integer, FullToken> fullTokenListWithPos(final char[] code) {
		// TODO Duplicate Code
		final PublicScanner scanner = prepareScanner();
		final SortedMap<Integer, FullToken> tokens = Maps.newTreeMap();
		tokens.put(-1, new FullToken(SENTENCE_START, SENTENCE_START));
		tokens.put(Integer.MAX_VALUE, new FullToken(SENTENCE_END, SENTENCE_END));
		scanner.setSource(code);
		while (!scanner.atEnd()) {
			do {
				try {
					final int token = scanner.getNextToken();
					if (token == ITerminalSymbols.TokenNameEOF) {
						break;
					}
					final String nxtToken = transformToken(token,
							scanner.getCurrentTokenString());
					final int position = scanner.getCurrentTokenStartPosition();
					tokens.put(position,
							new FullToken(nxtToken, Integer.toString(token)));
				} catch (final InvalidInputException e) {
					LOGGER.warning(ExceptionUtils.getFullStackTrace(e));
				}
			} while (!scanner.atEnd());

		}
		return tokens;
	}

	@Override
	public AbstractFileFilter getFileFilter() {
		return javaCodeFileFilter;
	}

	@Override
	public String getIdentifierType() {
		return IDENTIFIER_ID;
	}

	@Override
	public FullToken getTokenFromString(final String token) {
		if (token.equals(ITokenizer.SENTENCE_START)) {
			return new FullToken(ITokenizer.SENTENCE_START,
					ITokenizer.SENTENCE_START);
		}

		if (token.equals(ITokenizer.SENTENCE_END)) {
			return new FullToken(ITokenizer.SENTENCE_END,
					ITokenizer.SENTENCE_END);
		}
		return getTokenListFromCode(token.toCharArray()).get(1);
	}

	@Override
	public List<FullToken> getTokenListFromCode(final char[] code) {
		final List<FullToken> tokens = Lists.newArrayList();
		tokens.add(new FullToken(SENTENCE_START, SENTENCE_START));
		final PublicScanner scanner = prepareScanner();
		scanner.setSource(code);
		do {
			try {
				final int token = scanner.getNextToken();
				if (token == ITerminalSymbols.TokenNameEOF) {
					break;
				}
				final String nxtToken = transformToken(token,
						scanner.getCurrentTokenString());

				tokens.add(new FullToken(stripTokenIfNeeded(nxtToken), Integer
						.toString(token)));
			} catch (final InvalidInputException e) {
				LOGGER.warning(ExceptionUtils.getFullStackTrace(e));
			} catch (final StringIndexOutOfBoundsException e) {
				LOGGER.warning(ExceptionUtils.getFullStackTrace(e));
			}
		} while (!scanner.atEnd());
		tokens.add(new FullToken(SENTENCE_END, SENTENCE_END));
		return tokens;
	}

	/**
	 * Create the scanner.
	 * 
	 * @return
	 */
	protected PublicScanner prepareScanner() {
		final PublicScanner scanner = new PublicScanner();
		scanner.tokenizeComments = tokenizeComments;
		return scanner;
	}

	/**
	 * @param token
	 * @return
	 */
	protected String stripTokenIfNeeded(final String token) {
		return token.replace('\n', ' ').replace('\t', ' ').replace('\r', ' ')
				.replace("\n", " ").replace("\t", " ").replace("\r", " ")
				.replace("\'\\\\\'", "\'|\'").replace("\\", "|");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.javacodeutils.ITokenizer#tokenListFromCode(char[])
	 */
	@Override
	public List<String> tokenListFromCode(final char[] code) {
		final PublicScanner scanner = prepareScanner();
		final List<String> tokens = Lists.newArrayList();
		tokens.add(SENTENCE_START);
		scanner.setSource(code);
		do {
			try {
				final int token = scanner.getNextToken();
				if (token == ITerminalSymbols.TokenNameEOF) {
					break;
				}
				final String nxtToken = transformToken(token,
						scanner.getCurrentTokenString());

				tokens.add(stripTokenIfNeeded(nxtToken));
			} catch (final InvalidInputException e) {
				LOGGER.warning(ExceptionUtils.getFullStackTrace(e));
			} catch (final StringIndexOutOfBoundsException e) {
				LOGGER.warning(ExceptionUtils.getFullStackTrace(e));
			}
		} while (!scanner.atEnd());
		tokens.add(SENTENCE_END);
		return tokens;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.javacodeutils.ITokenizer#tokenListWithPos(char[])
	 */
	@Override
	public SortedMap<Integer, String> tokenListWithPos(final char[] code) {
		final PublicScanner scanner = prepareScanner();
		final SortedMap<Integer, String> tokens = Maps.newTreeMap();
		tokens.put(-1, SENTENCE_START);
		tokens.put(Integer.MAX_VALUE, SENTENCE_END);
		scanner.setSource(code);
		while (!scanner.atEnd()) {
			do {
				try {
					final int token = scanner.getNextToken();
					if (token == ITerminalSymbols.TokenNameEOF) {
						break;
					}
					final String nxtToken = transformToken(token,
							scanner.getCurrentTokenString());
					final int position = scanner.getCurrentTokenStartPosition();
					tokens.put(position, stripTokenIfNeeded(nxtToken));
				} catch (final InvalidInputException e) {
					LOGGER.warning(ExceptionUtils.getFullStackTrace(e));
				}
			} while (!scanner.atEnd());

		}
		return tokens;
	}

	/**
	 * Function used to transform the tokens. Useful when overriding some tokens
	 * in subclasses.
	 * 
	 * @param tokenType
	 * @param token
	 * @return
	 */
	protected String transformToken(final int tokenType, final String token) {
		return token;
	}
}