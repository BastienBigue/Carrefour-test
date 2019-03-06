package com.zenika.test_carrefour;

import com.zenika.test_carrefour.utils.CLIOptions;

public class App {

    public static void main(String[] args) {
        final CLIOptions options = new CLIOptions(args);

        options.checkArgs();

        Workflow workflow = new Workflow();

        //Launch the workflow with the appropriate stage request.
        if (options.fileOpt().exists()) {
            if (options.isStage2Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.RequestableStage.STAGE2);
            } else if (options.isStage3Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.RequestableStage.STAGE3);
            } else if (options.isStage4_1Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.RequestableStage.STAGE4_1);
            } else if (options.isStage4_2Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.RequestableStage.STAGE4_2);
            } else if (options.isStage4_3Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.RequestableStage.STAGE4_3);
            } else if (options.isStage4_4Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.RequestableStage.STAGE4_4);
            } else if (options.isStage5_1Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.RequestableStage.STAGE5_1);
            } else if (options.isStage5_2Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.RequestableStage.STAGE5_2);
            } else if (options.isFullWorkflowCommand()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.RequestableStage.ALL);
            }
        }
    }
}
