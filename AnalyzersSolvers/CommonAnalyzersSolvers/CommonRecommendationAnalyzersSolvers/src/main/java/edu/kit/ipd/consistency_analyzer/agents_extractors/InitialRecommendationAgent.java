package edu.kit.ipd.consistency_analyzer.agents_extractors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kohsuke.MetaInfServices;

import edu.kit.ipd.consistency_analyzer.agents_extractors.agents.Agent;
import edu.kit.ipd.consistency_analyzer.agents_extractors.agents.AgentDatastructure;
import edu.kit.ipd.consistency_analyzer.agents_extractors.agents.Configuration;
import edu.kit.ipd.consistency_analyzer.agents_extractors.agents.DependencyType;
import edu.kit.ipd.consistency_analyzer.agents_extractors.agents.Loader;
import edu.kit.ipd.consistency_analyzer.agents_extractors.agents.RecommendationAgent;
import edu.kit.ipd.consistency_analyzer.agents_extractors.extractors.IExtractor;
import edu.kit.ipd.consistency_analyzer.agents_extractors.extractors.RecommendationExtractor;
import edu.kit.ipd.consistency_analyzer.datastructures.IModelState;
import edu.kit.ipd.consistency_analyzer.datastructures.IRecommendationState;
import edu.kit.ipd.consistency_analyzer.datastructures.IText;
import edu.kit.ipd.consistency_analyzer.datastructures.ITextState;
import edu.kit.ipd.consistency_analyzer.datastructures.IWord;

@MetaInfServices(RecommendationAgent.class)
public class InitialRecommendationAgent extends RecommendationAgent {

    private List<IExtractor> extractors = new ArrayList<>();

    public InitialRecommendationAgent(IText text, ITextState textState, IModelState modelState, IRecommendationState recommendationState,
            GenericRecommendationConfig config) {
        super(DependencyType.TEXT_MODEL_RECOMMENDATION, text, textState, modelState, recommendationState);
        initializeAgents(config.recommendationExtractors);
    }

    public InitialRecommendationAgent(IText text, ITextState textState, IModelState modelState, IRecommendationState recommendationState) {
        this(text, textState, modelState, recommendationState, GenericRecommendationConfig.DEFAULT_CONFIG);
    }

    public InitialRecommendationAgent(AgentDatastructure data) {
        this(data.getText(), data.getTextState(), data.getModelState(), data.getRecommendationState());
    }

    public InitialRecommendationAgent() {
        super(DependencyType.TEXT_MODEL_RECOMMENDATION);
    }

    private void initializeAgents(List<String> extractorList) {
        Map<String, RecommendationExtractor> loadedExtractors = Loader.loadLoadable(RecommendationExtractor.class);

        for (String recommendationExtractor : extractorList) {
            if (!loadedExtractors.containsKey(recommendationExtractor)) {
                throw new IllegalArgumentException("RecommendationExtractor " + recommendationExtractor + " not found");
            }
            extractors.add(loadedExtractors.get(recommendationExtractor).create(textState, modelState, recommendationState));
        }

    }

    @Override
    public RecommendationAgent create(AgentDatastructure data, Configuration config) {
        return create(data.getText(), data.getTextState(), data.getModelState(), data.getRecommendationState(), config);
    }

    @Override
    public RecommendationAgent create(IText text, ITextState textState, IModelState modelState, IRecommendationState recommendationState,
            Configuration config) {
        return new InitialRecommendationAgent(text, textState, modelState, recommendationState, (GenericRecommendationConfig) config);
    }

    @Override
    public void exec() {

        for (IWord word : text.getWords()) { for (IExtractor extractor : extractors) { extractor.exec(word); } }
    }

    @Override
    public RecommendationAgent create(IText text, ITextState textState, IModelState modelState, IRecommendationState recommendationState) {
        return create(text, textState, modelState, recommendationState, GenericRecommendationConfig.DEFAULT_CONFIG);
    }

    @Override
    public Agent create(AgentDatastructure data) {
        return create(data, GenericRecommendationConfig.DEFAULT_CONFIG);
    }
}
