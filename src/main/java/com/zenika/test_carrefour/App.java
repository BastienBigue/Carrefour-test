package com.zenika.test_carrefour;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.zenika.test_carrefour.utils.CLIOptions;

public class App {

    //TODO Check if result file exists before compute
    //TODO check if lines are well formatted

    public static void main(String[] args) {
        final CLIOptions options = new CLIOptions(args);

        options.checkArgs();

        Workflow workflow = new Workflow();

        if (options.fileOpt().exists()) {
            if (options.isStage2Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.REQUESTABLE_STAGES.STAGE2);
            } else if (options.isStage3Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.REQUESTABLE_STAGES.STAGE3);
            } else if (options.isStage4_1Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.REQUESTABLE_STAGES.STAGE4_1);
            } else if (options.isStage4_2Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.REQUESTABLE_STAGES.STAGE4_2);
            } else if (options.isStage4_3Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.REQUESTABLE_STAGES.STAGE4_3);
            } else if (options.isStage4_4Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.REQUESTABLE_STAGES.STAGE4_4);
            } else if (options.isStage5_1Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.REQUESTABLE_STAGES.STAGE5_1);
            } else if (options.isStage5_2Command()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.REQUESTABLE_STAGES.STAGE5_2);
            } else if (options.isFullWorkflowCommand()) {
                workflow.processWorkflow(options.fileOpt(), options.topNOpt(), Workflow.REQUESTABLE_STAGES.ALL);
            }
        }
    }
}
