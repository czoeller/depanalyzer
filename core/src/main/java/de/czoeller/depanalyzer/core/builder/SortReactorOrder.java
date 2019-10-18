package de.czoeller.depanalyzer.core.builder;

import com.google.common.collect.Ordering;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.project.MavenProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SortReactorOrder {

    private final ReactorOrderParser reactorOrderParser;

    public SortReactorOrder() {
        this.reactorOrderParser = new ReactorOrderParser();
    }

    public void sortModulesInReactorOrder(BuiltProject builtProject, List<MavenProject> subModuleList) {
        log.debug("Try to bring modules to reactor order");
        List<String> modules = reactorOrderParser.parseModulesInReactorOrder(builtProject.getMavenLog());
        if(modules.isEmpty()) {
            log.debug("Project seems to be no multi module project.");
        } else {
            log.debug("order parsed from reactor: {}", modules);
            log.debug("order before sort: {}", subModuleList.stream().map(MavenProject::getName).collect(Collectors.toList()));
            subModuleList.sort(Ordering.explicit(modules).onResultOf(MavenProject::getName));
            final List<String> modulesAfterSort = subModuleList.stream().map(MavenProject::getName).collect(Collectors.toList());
            log.debug("order after sort: {}", modulesAfterSort);

            if(!modulesAfterSort.equals(modules)) {
                log.error("The reactor order could not be reconstructed! This would lead to wrong handling in the reachability map of the graph building component.");
                System.exit(1);
            }
        }
    }
}
