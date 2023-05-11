/* Licensed under MIT 2023. */
package edu.kit.kastel.mcse.ardoco.core.codetraceability;

import java.util.Collection;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.ImmutableSet;

import edu.kit.kastel.mcse.ardoco.core.api.codetraceability.CodeTraceabilityState;
import edu.kit.kastel.mcse.ardoco.core.api.models.tracelinks.SamCodeTraceLink;
import edu.kit.kastel.mcse.ardoco.core.api.models.tracelinks.TransitiveTraceLink;
import edu.kit.kastel.mcse.ardoco.core.data.AbstractState;

public class CodeTraceabilityStateImpl extends AbstractState implements CodeTraceabilityState {

    private transient MutableList<SamCodeTraceLink> samCodeTraceLinks = Lists.mutable.empty();
    private transient MutableList<TransitiveTraceLink> transitiveTraceLinks = Lists.mutable.empty();

    @Override
    public boolean addSamCodeTraceLink(SamCodeTraceLink traceLink) {
        return this.samCodeTraceLinks.add(traceLink);
    }

    @Override
    public boolean addSamCodeTraceLinks(Collection<SamCodeTraceLink> traceLinks) {
        return this.samCodeTraceLinks.addAll(traceLinks);
    }

    @Override
    public ImmutableSet<SamCodeTraceLink> getSamCodeTraceLinks() {
        return this.samCodeTraceLinks.toImmutableSet();
    }

    @Override
    public boolean addTransitiveTraceLinks(TransitiveTraceLink traceLink) {
        return this.transitiveTraceLinks.add(traceLink);
    }

    @Override
    public boolean addTransitiveTraceLinks(Collection<TransitiveTraceLink> traceLinks) {
        return this.transitiveTraceLinks.addAll(traceLinks);
    }

    @Override
    public ImmutableSet<TransitiveTraceLink> getTransitiveTraceLinks() {
        return this.transitiveTraceLinks.toImmutableSet();
    }

}
