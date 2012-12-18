package ee.midaiganes.portletsservices.webcontent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.VariablesMap;
import org.thymeleaf.templatemode.TemplateModeHandler;
import org.thymeleaf.templateparser.xmlsax.XmlNonValidatingSAXTemplateParser;
import org.thymeleaf.templatewriter.AbstractGeneralTemplateWriter;

@Service(value = "templateEngineService")
public class TemplateEngineService implements InitializingBean {

	private final TemplateEngine templateEngine;

	@Resource
	private TemplateResolver templateResolver;

	@Resource
	private WebContentRepository webContentRepository;

	public TemplateEngineService() {
		templateEngine = new TemplateEngine();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.setTemplateModeHandlers(getTemplateModeHandlers());
	}

	private HashSet<TemplateModeHandler> getTemplateModeHandlers() {
		return new HashSet<>(Arrays.asList(new TemplateModeHandler("XML", new XmlNonValidatingSAXTemplateParser(getPoolSize()), new TemplateWriter())));
	}

	private int getPoolSize() {
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		int MAX_PARSERS_POOL_SIZE = 24;
		return Math.min((availableProcessors <= 2 ? availableProcessors : availableProcessors - 1), MAX_PARSERS_POOL_SIZE);
	}

	public String process(WebContent webContent) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("webcontenttitle", webContent.getTitle());
		List<WebContentField> fields = webContentRepository.getWebContentFields(webContent.getId());
		List<StructureField> structureFields = webContentRepository.getStructureFields(webContent.getStructureId());
		for (WebContentField field : fields) {
			for (StructureField f : structureFields) {
				if (field.getStructureFieldId() == f.getId()) {
					variables.put(f.getFieldName(), field.getFieldValue());
					break;
				}
			}
		}
		return templateEngine.process(Long.toString(webContent.getTemplateId()), new Context(variables));
	}

	private static final class TemplateWriter extends AbstractGeneralTemplateWriter {
		@Override
		protected boolean useXhtmlTagMinimizationRules() {
			return false;
		}

		@Override
		protected boolean shouldWriteXmlDeclaration() {
			return false;
		}
	}

	private static final class Context implements IContext {
		private final VariablesMap<String, Object> variables;

		public Context(Map<String, Object> variables) {
			this.variables = new VariablesMap<String, Object>(variables);
		}

		@Override
		public VariablesMap<String, Object> getVariables() {
			return variables;
		}

		@Override
		public Locale getLocale() {
			// TODO
			return Locale.ENGLISH;
		}

		@Override
		public void addContextExecutionInfo(String templateName) {
		}
	}
}
