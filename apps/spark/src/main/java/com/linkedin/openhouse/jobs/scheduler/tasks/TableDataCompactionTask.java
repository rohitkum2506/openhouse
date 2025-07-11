package com.linkedin.openhouse.jobs.scheduler.tasks;

import com.linkedin.openhouse.jobs.client.JobsClient;
import com.linkedin.openhouse.jobs.client.TablesClient;
import com.linkedin.openhouse.jobs.client.model.JobConf;
import com.linkedin.openhouse.jobs.util.TableMetadata;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A task to rewrite data files in a table.
 *
 * @see <a href="https://iceberg.apache.org/docs/latest/maintenance/#compact-data-files">Compact
 *     data files</a>
 */
public class TableDataCompactionTask extends TableOperationTask<TableMetadata> {
  public static final JobConf.JobTypeEnum OPERATION_TYPE = JobConf.JobTypeEnum.DATA_COMPACTION;

  public TableDataCompactionTask(
      JobsClient jobsClient,
      TablesClient tablesClient,
      TableMetadata metadata,
      long pollIntervalMs,
      long queuedTimeoutMs,
      long taskTimeoutMs) {
    super(jobsClient, tablesClient, metadata, pollIntervalMs, queuedTimeoutMs, taskTimeoutMs);
  }

  public TableDataCompactionTask(
      JobsClient jobsClient, TablesClient tablesClient, TableMetadata metadata) {
    super(jobsClient, tablesClient, metadata);
  }

  @Override
  public JobConf.JobTypeEnum getType() {
    return OPERATION_TYPE;
  }

  @Override
  protected List<String> getArgs() {
    return Stream.of("--tableName", metadata.fqtn()).collect(Collectors.toList());
  }

  @Override
  protected boolean shouldRun() {
    return metadata.isPrimary() && (metadata.isTimePartitioned() || metadata.isClustered());
  }
}
