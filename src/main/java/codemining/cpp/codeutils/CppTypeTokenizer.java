/**
 * 
 */
package codemining.cpp.codeutils;

import java.util.List;
import java.util.SortedMap;

import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.NotImplementedException;
import org.eclipse.cdt.internal.formatter.scanner.Scanner;
import org.eclipse.cdt.internal.formatter.scanner.Token;

import codemining.languagetools.ITokenizer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A C/C++ token type tokenizer
 * 
 * @author Miltos Allamanis
 * 
 */
public class CppTypeTokenizer implements ITokenizer {

	private static final long serialVersionUID = 8831418782380291930L;

	public static String TYPE_LITERAL = "%LITERAL%";
	public static String TYPE_IDENTIFIER = "%IDENTIFIER%";
	public static String TYPE_COMMENT = "%COMMENT%";
	public static String TYPE_PREPROCESSOR = "%PREPROCESSOR%";

	private static final RegexFileFilter cppCodeFilter = new RegexFileFilter(
			".*\\.(cc|cpp|h)$");

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemining.languagetools.ITokenizer#fullTokenListWithPos(char[])
	 */
	@Override
	public SortedMap<Integer, FullToken> fullTokenListWithPos(char[] code) {
		final SortedMap<Integer, FullToken> tokens = Maps.newTreeMap();
		tokens.put(-1, new FullToken(SENTENCE_START, SENTENCE_START));
		tokens.put(Integer.MAX_VALUE, new FullToken(SENTENCE_END, SENTENCE_END));

		final Scanner scanner = new Scanner();
		scanner.setSource(code);
		do {
			final int token = scanner.getNextToken();
			if (token == Token.tWHITESPACE) {
				continue;
			}
			final String nxtToken = new String(scanner.getCurrentTokenSource());
			tokens.put(scanner.getCurrentPosition(), new FullToken(
					getTokenType(token, nxtToken), Integer.toString(token)));
		} while (!scanner.atEnd());
		return tokens;
	}

	/**
	 * @param token
	 * @param nxtToken
	 * @return
	 */
	public String getTokenType(final int token, String nxtToken) {
		if (token == Token.tIDENTIFIER) {
			return TYPE_IDENTIFIER;
		} else if (token == Token.tBLOCKCOMMENT || token == Token.tLINECOMMENT) {
			return TYPE_COMMENT;
		} else if (token == Token.tSTRING) {
			return TYPE_LITERAL;
		} else if (token == Token.tINTEGER || token == Token.tFLOATINGPT) {
			return TYPE_LITERAL;
		} else if (token == Token.tPREPROCESSOR
				|| token == Token.tPREPROCESSOR_DEFINE
				|| token == Token.tPREPROCESSOR_INCLUDE) {
			return TYPE_PREPROCESSOR;
		}
		return nxtToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemining.languagetools.ITokenizer#getFileFilter()
	 */
	@Override
	public AbstractFileFilter getFileFilter() {
		return cppCodeFilter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemining.languagetools.ITokenizer#getIdentifierType()
	 */
	@Override
	public String getIdentifierType() {
		throw new NotImplementedException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * codemining.languagetools.ITokenizer#getTokenFromString(java.lang.String)
	 */
	@Override
	public FullToken getTokenFromString(String token) {
		if (token.equals(ITokenizer.SENTENCE_START)) {
			return new FullToken(ITokenizer.SENTENCE_START, SENTENCE_START);
		}

		if (token.equals(ITokenizer.SENTENCE_END)) {
			return new FullToken(ITokenizer.SENTENCE_END, SENTENCE_END);
		}
		return getTokenListFromCode(token.toCharArray()).get(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemining.languagetools.ITokenizer#getTokenListFromCode(char[])
	 */
	@Override
	public List<FullToken> getTokenListFromCode(char[] code) {
		final List<FullToken> tokens = Lists.newArrayList();
		tokens.add(new FullToken(SENTENCE_START, SENTENCE_START));

		final Scanner scanner = new Scanner();
		scanner.setSource(code);

		do {
			final int token = scanner.getNextToken();
			if (token == Token.tWHITESPACE) {
				continue;
			}
			final String nxtToken = new String(scanner.getCurrentTokenSource());
			tokens.add(new FullToken(getTokenType(token, nxtToken), Integer
					.toString(token)));
		} while (!scanner.atEnd());

		tokens.add(new FullToken(SENTENCE_END, SENTENCE_END));
		return tokens;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemining.languagetools.ITokenizer#tokenListFromCode(char[])
	 */
	@Override
	public List<String> tokenListFromCode(char[] code) {
		final List<String> tokens = Lists.newArrayList();
		tokens.add(SENTENCE_START);

		final Scanner scanner = new Scanner();
		scanner.setSource(code);

		do {
			final int token = scanner.getNextToken();
			if (token == Token.tWHITESPACE) {
				continue;
			}
			final String nxtToken = new String(scanner.getCurrentTokenSource());
			tokens.add(getTokenType(token, nxtToken));
		} while (!scanner.atEnd());

		tokens.add(SENTENCE_END);
		return tokens;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemining.languagetools.ITokenizer#tokenListWithPos(char[])
	 */
	@Override
	public SortedMap<Integer, String> tokenListWithPos(char[] code) {
		final SortedMap<Integer, String> tokens = Maps.newTreeMap();
		tokens.put(-1, SENTENCE_START);
		tokens.put(Integer.MAX_VALUE, SENTENCE_END);

		final Scanner scanner = new Scanner();
		scanner.setSource(code);
		do {
			final int token = scanner.getNextToken();
			if (token == Token.tWHITESPACE) {
				continue;
			}
			final String nxtToken = new String(scanner.getCurrentTokenSource());
			tokens.put(scanner.getCurrentPosition(),
					getTokenType(token, nxtToken));
		} while (!scanner.atEnd());
		return tokens;
	}

}