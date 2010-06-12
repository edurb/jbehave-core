package org.jbehave.examples.trader;

import static java.util.Arrays.asList;
import static org.jbehave.core.reporters.StoryReporterBuilder.Format.CONSOLE;
import static org.jbehave.core.reporters.StoryReporterBuilder.Format.HTML;
import static org.jbehave.core.reporters.StoryReporterBuilder.Format.TXT;
import static org.jbehave.core.reporters.StoryReporterBuilder.Format.XML;

import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryPathFinder;
import org.jbehave.core.parsers.RegexPrefixCapturingPatternParser;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.SilentStepMonitor;
import org.jbehave.core.steps.StepsFactory;
import org.jbehave.examples.trader.converters.TraderConverter;
import org.jbehave.examples.trader.model.Stock;
import org.jbehave.examples.trader.model.Trader;
import org.jbehave.examples.trader.persistence.TraderPersister;
import org.jbehave.examples.trader.service.TradingService;

/**
 * Specifies the Embedder for the Trader example, providing the
 * Configuration and the CandidateSteps, using classpath story loading.
 */
public class ClasspathTraderEmbedder extends Embedder {

	@Override
	public Configuration configuration() {
		Class<? extends ClasspathTraderEmbedder> embedderClass = this.getClass();
		return new MostUsefulConfiguration()
			.useStoryLoader(new LoadFromClasspath(embedderClass.getClassLoader()))
			.useStoryReporterBuilder(new StoryReporterBuilder()
        		// use absolute output directory with Ant
        		//.withOutputDirectory("target/jbehave-reports").withOutputAbsolute(true)
        		.withOutputLocationClass(embedderClass)
        		.withDefaultFormats()
				.withFormats(CONSOLE, TXT, HTML, XML))
			.buildReporters(storyPaths())
			.useEmbedderControls(new EmbedderControls()
					.doIgnoreFailureInStories(true).doIgnoreFailureInReports(true))
			.useParameterConverters(new ParameterConverters(
							new TraderConverter(mockTradePersister())))
			.useStepPatternParser(new RegexPrefixCapturingPatternParser(
							"%")) // use '%' instead of '$' to identify parameters
			.useStepMonitor(new SilentStepMonitor());
					
	}

	@Override
	public List<CandidateSteps> candidateSteps() {
		return asList(new StepsFactory(configuration())
				.createCandidateSteps(new TraderSteps(new TradingService()),
						new BeforeAfterSteps()));
	}

	protected TraderPersister mockTradePersister() {
		return new TraderPersister(new Trader("Mauro", asList(new Stock("STK1",
				10.d))));
	}

	public List<String> storyPaths() {
		StoryPathFinder finder = new StoryPathFinder();
		return finder.listStoryPaths("target/classes", "",
				asList("**/*.story"), asList(""));
	}

}