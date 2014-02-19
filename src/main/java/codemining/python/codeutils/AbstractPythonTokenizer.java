package codemining.python.codeutils;

import java.util.List;
import java.util.SortedMap;

import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.python.pydev.parser.grammarcommon.ITokenManager;
import org.python.pydev.parser.jython.FastCharStream;
import org.python.pydev.parser.jython.Token;

import codemining.languagetools.ITokenizer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * An abstract python tokenizer using the PyDev interface.
 * 
 * @author Miltos Allamanis <m.allamanis@ed.ac.uk>
 * 
 */
public abstract class AbstractPythonTokenizer implements ITokenizer {

	private static final long serialVersionUID = 5009530263783901964L;

	public abstract ITokenManager getPythonTokenizer(final FastCharStream stream);

	/**
	 * A filter for the files being tokenized.
	 */
	private static final RegexFileFilter pythonCodeFilter = new RegexFileFilter(
			".*\\.py$");

	public AbstractPythonTokenizer() {
		super();
	}

	@Override
	public SortedMap<Integer, FullToken> fullTokenListWithPos(char[] code) {
		final FastCharStream stream = new FastCharStream(code);
		final ITokenManager mng = getPythonTokenizer(stream);
		final SortedMap<Integer, FullToken> tokens = Maps.newTreeMap();

		Token nextToken = mng.getNextToken();
		while (nextToken.kind != 0) {
			if (shouldAdd(nextToken)) {
				// TODO: Bad Heurisitc...
				tokens.put(
						nextToken.getBeginLine() * 500
								+ nextToken.getBeginCol(),
						new FullToken(nextToken.image, Integer
								.toString(nextToken.kind)));
			}
			nextToken = mng.getNextToken();
		}

		return tokens;
	}

	@Override
	public AbstractFileFilter getFileFilter() {
		return pythonCodeFilter;
	}

	@Override
	public String getIdentifierType() {
		return "94"; // TODO from not hard coded?
	}

	@Override
	public FullToken getTokenFromString(String token) {
		final FastCharStream stream = new FastCharStream(token.toCharArray());
		final ITokenManager mng = getPythonTokenizer(stream);
		final Token pyToken = mng.getNextToken();
		return new FullToken(pyToken.image, Integer.toString(pyToken.kind));
	}

	@Override
	public List<FullToken> getTokenListFromCode(final char[] code) {
		final FastCharStream stream = new FastCharStream(code);
		final ITokenManager mng = getPythonTokenizer(stream);
		final List<FullToken> tokens = Lists.newArrayList();

		Token nextToken = mng.getNextToken();
		while (nextToken.kind != 0) {
			if (shouldAdd(nextToken)) {
				tokens.add(new FullToken(nextToken.image, Integer
						.toString(nextToken.kind)));
			}
			nextToken = mng.getNextToken();
		}

		return tokens;
	}

	/**
	 * @param nextToken
	 * @return
	 */
	public boolean shouldAdd(Token nextToken) {
		// disallow whitespace, indent and docstrings
		return nextToken.kind != 6 && nextToken.kind != 14
				&& nextToken.kind != 13 && nextToken.kind != 115;
	}

	@Override
	public List<String> tokenListFromCode(char[] code) {
		final FastCharStream stream = new FastCharStream(code);
		final ITokenManager mng = getPythonTokenizer(stream);
		final List<String> tokens = Lists.newArrayList();

		Token nextToken = mng.getNextToken();
		while (nextToken.kind != 0) {
			if (shouldAdd(nextToken)) {
				tokens.add(nextToken.image);
			}
			nextToken = mng.getNextToken();
		}

		return tokens;
	}

	@Override
	public SortedMap<Integer, String> tokenListWithPos(char[] code) {
		final FastCharStream stream = new FastCharStream(code);
		final ITokenManager mng = getPythonTokenizer(stream);
		final SortedMap<Integer, String> tokens = Maps.newTreeMap();

		Token nextToken = mng.getNextToken();
		while (nextToken.kind != 0) {
			if (shouldAdd(nextToken)) {
				// TODO: Bad Heurisitc...
				tokens.put(
						nextToken.getBeginLine() * 500
								+ nextToken.getBeginCol(), nextToken.image);
			}
			nextToken = mng.getNextToken();
		}

		return tokens;
	}

}