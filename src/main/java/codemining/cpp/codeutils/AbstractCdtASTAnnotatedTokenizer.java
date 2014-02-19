/**
 * 
 */
package codemining.cpp.codeutils;

import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.logging.Logger;

import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.c.ICASTDesignator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCapture;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.internal.core.dom.parser.ASTAmbiguousNode;
import org.eclipse.core.runtime.CoreException;

import codemining.languagetools.ITokenizer;
import codemining.util.SettingsLoader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * C/C++ AST Annotated Tokenizer
 * 
 * @author Miltos Allamanis <m.allamanis@ed.ac.uk>
 * 
 */
public abstract class AbstractCdtASTAnnotatedTokenizer implements ITokenizer {

	private class TokenDecorator extends ASTVisitor {
		final SortedMap<Integer, FullToken> baseTokens;
		final SortedMap<Integer, FullToken> annotatedTokens;

		public TokenDecorator(final SortedMap<Integer, FullToken> baseTokens) {
			super(true);
			this.baseTokens = baseTokens;
			annotatedTokens = Maps.newTreeMap();
		}

		SortedMap<Integer, FullToken> getAnnotatedTokens(final IASTNode node) {
			annotatedTokens.putAll(baseTokens);
			node.accept(this);
			return annotatedTokens;
		}

		public void preVisit(final IASTNode node) {
			final IASTFileLocation fileLocation = node.getFileLocation();

			final int fromPosition = fileLocation.getNodeOffset();
			final int endPosition = fromPosition + fileLocation.getNodeLength();
			final String nodeType = node.getClass().getSimpleName();
			final String parentType;
			if (node.getParent() != null) {
				parentType = node.getParent().getClass().getSimpleName();
			} else {
				parentType = "NONE";
			}
			final SortedMap<Integer, FullToken> nodeTokens = baseTokens.subMap(
					fromPosition, endPosition);
			for (final Entry<Integer, FullToken> token : nodeTokens.entrySet()) {
				if (token.getValue().token.startsWith("WS_")) {
					annotatedTokens.put(
							token.getKey(),
							new FullToken(token.getValue().token, token
									.getValue().tokenType));
				} else {
					annotatedTokens.put(
							token.getKey(),
							new FullToken(token.getValue().token + "_i:"
									+ nodeType + "_p:" + parentType, token
									.getValue().tokenType));
				}
			}
		}

		@Override
		public int visit(final ASTAmbiguousNode astAmbiguousNode) {
			preVisit(astAmbiguousNode);
			return super.visit(astAmbiguousNode);
		}

		@Override
		public int visit(final IASTArrayModifier arrayModifier) {
			preVisit(arrayModifier);
			return super.visit(arrayModifier);
		}

		@Override
		public int visit(final IASTDeclaration declaration) {
			preVisit(declaration);
			return super.visit(declaration);
		}

		@Override
		public int visit(final IASTDeclarator declarator) {
			preVisit(declarator);
			return super.visit(declarator);
		}

		@Override
		public int visit(final IASTDeclSpecifier declSpec) {
			preVisit(declSpec);
			return super.visit(declSpec);
		}

		@Override
		public int visit(final IASTEnumerator enumerator) {
			preVisit(enumerator);
			return super.visit(enumerator);
		}

		@Override
		public int visit(final IASTExpression expression) {
			preVisit(expression);
			return super.visit(expression);
		}

		@Override
		public int visit(final IASTInitializer initializer) {
			preVisit(initializer);
			return super.visit(initializer);
		}

		@Override
		public int visit(final IASTName name) {
			preVisit(name);
			return super.visit(name);
		}

		@Override
		public int visit(final IASTParameterDeclaration parameterDeclaration) {
			preVisit(parameterDeclaration);
			return super.visit(parameterDeclaration);
		}

		@Override
		public int visit(final IASTPointerOperator ptrOperator) {
			preVisit(ptrOperator);
			return super.visit(ptrOperator);
		}

		@Override
		public int visit(final IASTProblem problem) {
			preVisit(problem);
			return super.visit(problem);
		}

		@Override
		public int visit(final IASTStatement statement) {
			preVisit(statement);
			return super.visit(statement);
		}

		@Override
		public int visit(final IASTTranslationUnit tu) {
			preVisit(tu);
			return super.visit(tu);
		}

		@Override
		public int visit(final IASTTypeId typeId) {
			preVisit(typeId);
			return super.visit(typeId);
		}

		@Override
		public int visit(final ICASTDesignator designator) {
			preVisit(designator);
			return super.visit(designator);
		}

		@Override
		public int visit(final ICPPASTBaseSpecifier baseSpecifier) {
			preVisit(baseSpecifier);
			return super.visit(baseSpecifier);
		}

		@Override
		public int visit(final ICPPASTCapture capture) {
			preVisit(capture);
			return super.visit(capture);
		}

		@Override
		public int visit(final ICPPASTNamespaceDefinition namespaceDefinition) {
			preVisit(namespaceDefinition);
			return super.visit(namespaceDefinition);
		}

		@Override
		public int visit(final ICPPASTTemplateParameter templateParameter) {
			preVisit(templateParameter);
			return super.visit(templateParameter);
		}
	}

	private static final long serialVersionUID = -3086123070064967257L;

	public final ITokenizer baseTokenizer;

	private static final Logger LOGGER = Logger
			.getLogger(AbstractCdtASTAnnotatedTokenizer.class.getName());

	final Class<? extends ICdtAstExtractor> astExtractorClass;

	public AbstractCdtASTAnnotatedTokenizer(
			final Class<? extends ICdtAstExtractor> extractorClass) {
		astExtractorClass = extractorClass;
		try {
			final Class<? extends ITokenizer> tokenizerClass = (Class<? extends ITokenizer>) Class
					.forName(SettingsLoader.getStringSetting("baseTokenizer",
							"codemining.cpp.codeutils.CDTTokenizer"));
			baseTokenizer = tokenizerClass.newInstance();
		} catch (final ClassNotFoundException e) {
			LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
			throw new IllegalArgumentException(e);
		} catch (final InstantiationException e) {
			LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
			throw new IllegalArgumentException(e);
		} catch (final IllegalAccessException e) {
			LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
			throw new IllegalArgumentException(e);
		}
	}

	public AbstractCdtASTAnnotatedTokenizer(final ITokenizer base,
			final Class<? extends ICdtAstExtractor> extractorClass) {
		astExtractorClass = extractorClass;
		baseTokenizer = base;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemining.languagetools.ITokenizer#fullTokenListWithPos(char[])
	 */
	@Override
	public SortedMap<Integer, FullToken> fullTokenListWithPos(final char[] code) {
		throw new NotImplementedException();
	}

	private SortedMap<Integer, FullToken> getAnnotatedTokens(final char[] code) {
		try {
			final ICdtAstExtractor ex = astExtractorClass.newInstance();
			final IASTTranslationUnit cu = ex.getAST(code);

			final SortedMap<Integer, FullToken> baseTokens = baseTokenizer
					.fullTokenListWithPos(code);
			final TokenDecorator dec = new TokenDecorator(baseTokens);
			return dec.getAnnotatedTokens(cu);
		} catch (final CoreException ce) {
			LOGGER.severe("Failed to get annotated tokens, because "
					+ ExceptionUtils.getFullStackTrace(ce));
		} catch (final InstantiationException e) {
			LOGGER.severe("Failed to get annotated tokens, because "
					+ ExceptionUtils.getFullStackTrace(e));
		} catch (final IllegalAccessException e) {
			LOGGER.severe("Failed to get annotated tokens, because "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemining.languagetools.ITokenizer#getFileFilter()
	 */
	@Override
	public AbstractFileFilter getFileFilter() {
		return baseTokenizer.getFileFilter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemining.languagetools.ITokenizer#getIdentifierType()
	 */
	@Override
	public String getIdentifierType() {
		return baseTokenizer.getIdentifierType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * codemining.languagetools.ITokenizer#getTokenFromString(java.lang.String)
	 */
	@Override
	public FullToken getTokenFromString(final String token) {
		throw new IllegalArgumentException(
				"ASTAnnotatedTokenizer cannot return a token from a single string.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemining.languagetools.ITokenizer#getTokenListFromCode(char[])
	 */
	@Override
	public List<FullToken> getTokenListFromCode(final char[] code) {
		final List<FullToken> tokens = Lists.newArrayList();
		for (final Entry<Integer, FullToken> token : getAnnotatedTokens(code)
				.entrySet()) {
			tokens.add(token.getValue());
		}
		return tokens;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemining.languagetools.ITokenizer#tokenListFromCode(char[])
	 */
	@Override
	public List<String> tokenListFromCode(final char[] code) {
		final List<String> tokens = Lists.newArrayList();
		for (final Entry<Integer, FullToken> token : getAnnotatedTokens(code)
				.entrySet()) {
			tokens.add(token.getValue().token);
		}
		return tokens;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemining.languagetools.ITokenizer#tokenListWithPos(char[])
	 */
	@Override
	public SortedMap<Integer, String> tokenListWithPos(final char[] code) {
		final SortedMap<Integer, String> tokens = Maps.newTreeMap();
		for (final Entry<Integer, FullToken> token : getAnnotatedTokens(code)
				.entrySet()) {
			tokens.put(token.getKey(), token.getValue().token);
		}
		return tokens;
	}

}