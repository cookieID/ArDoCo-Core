package edu.kit.ipd.consistency_analyzer.agents_extractors;

import java.util.List;
import java.util.stream.Collectors;

import org.kohsuke.MetaInfServices;

import edu.kit.ipd.consistency_analyzer.agents_extractors.agents.Agent;
import edu.kit.ipd.consistency_analyzer.agents_extractors.agents.AgentDatastructure;
import edu.kit.ipd.consistency_analyzer.agents_extractors.agents.Configuration;
import edu.kit.ipd.consistency_analyzer.agents_extractors.agents.ConnectionAgent;
import edu.kit.ipd.consistency_analyzer.agents_extractors.agents.DependencyType;
import edu.kit.ipd.consistency_analyzer.common.SimilarityUtils;
import edu.kit.ipd.consistency_analyzer.datastructures.IConnectionState;
import edu.kit.ipd.consistency_analyzer.datastructures.IInstance;
import edu.kit.ipd.consistency_analyzer.datastructures.IModelState;
import edu.kit.ipd.consistency_analyzer.datastructures.IRecommendationState;
import edu.kit.ipd.consistency_analyzer.datastructures.IRecommendedInstance;
import edu.kit.ipd.consistency_analyzer.datastructures.IText;
import edu.kit.ipd.consistency_analyzer.datastructures.ITextState;

/**
 * This connector finds names of model instance in recommended instances.
 *
 * @author Sophie
 *
 */
@MetaInfServices(ConnectionAgent.class)
public class InstanceConnectionAgent extends ConnectionAgent {

    private double probability;
    private double probabilityWithoutType;

    /**
     * Creates a new InstanceMappingConnector.
     *
     * @param graph               the PARSE graph
     * @param textState           the text extraction state
     * @param modelState          the model extraction state
     * @param recommendationState the recommendation state
     * @param connectionState     the connection state
     */
    public InstanceConnectionAgent(//
            IText text, ITextState textState, IModelState modelState, IRecommendationState recommendationState, IConnectionState connectionState) {
        this(text, textState, modelState, recommendationState, connectionState, GenericConnectionAnalyzerSolverConfig.DEFAULT_CONFIG);
    }

    public InstanceConnectionAgent(//
            IText text, ITextState textState, IModelState modelState, IRecommendationState recommendationState, IConnectionState connectionState,
            GenericConnectionAnalyzerSolverConfig config) {
        super(DependencyType.MODEL_RECOMMENDATION_CONNECTION, text, textState, modelState, recommendationState, connectionState);
        probability = config.instanceConnectionSolverProbability;
        probabilityWithoutType = config.instanceConnectionSolverProbabilityWithoutType;
    }

    // Required for the service loader
    public InstanceConnectionAgent() {
        super(DependencyType.MODEL_RECOMMENDATION_CONNECTION);
    }

    public InstanceConnectionAgent(AgentDatastructure data, Configuration config) {
        this(data.getText(), data.getTextState(), data.getModelState(), data.getRecommendationState(), data.getConnectionState(),
                (GenericConnectionAnalyzerSolverConfig) config);
    }

    public InstanceConnectionAgent(IText text, ITextState textExtractionState, IModelState modelExtractionState, IRecommendationState recommendationState,
            IConnectionState connectionState, double probability, double probabilityWithoutType) {
        this(text, textExtractionState, modelExtractionState, recommendationState, connectionState);
        this.probability = probability;
        this.probabilityWithoutType = probabilityWithoutType;
    }

    @Override
    public ConnectionAgent create(IText text, ITextState textState, IModelState modelState, IRecommendationState recommendationState,
            IConnectionState connectionState, Configuration config) {
        return new InstanceConnectionAgent(text, textState, modelState, recommendationState, connectionState, (GenericConnectionAnalyzerSolverConfig) config);
    }

    /**
     * Executes the connector.
     */
    @Override
    public void exec() {

        findNamesOfModelInstancesInSupposedMappings();
    }

    /**
     * Seaches in the recommended instances of the recommendation state for similar names to extracted instances. If
     * some are found the instance link is added to the connection state.
     */
    private void findNamesOfModelInstancesInSupposedMappings() {
        List<IRecommendedInstance> ris = recommendationState.getRecommendedInstances();
        for (IInstance i : modelState.getInstances()) {
            List<IRecommendedInstance> mostLikelyRi = SimilarityUtils.getMostRecommendedInstancesToInstanceByReferences(i, ris);

            List<IRecommendedInstance> mostLikelyRiWithoutType = mostLikelyRi.stream()
                    .filter(ri -> !ri.getTypeMappings().isEmpty())
                    .collect(Collectors.toList());
            mostLikelyRiWithoutType.stream().forEach(ml -> connectionState.addToLinks(ml, i, probabilityWithoutType));
            mostLikelyRi.stream().forEach(ml -> connectionState.addToLinks(ml, i, probability));
        }
    }

    @Override
    public ConnectionAgent create(IText text, ITextState textState, IModelState modelState, IRecommendationState recommendationState,
            IConnectionState connectionState) {
        return create(text, textState, modelState, recommendationState, connectionState, GenericConnectionAnalyzerSolverConfig.DEFAULT_CONFIG);
    }

    @Override
    public Agent create(AgentDatastructure data) {
        return create(data, GenericConnectionAnalyzerSolverConfig.DEFAULT_CONFIG);
    }

}
