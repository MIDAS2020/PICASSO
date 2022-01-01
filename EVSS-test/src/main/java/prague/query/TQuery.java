package prague.query;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

/**
 * TQuery
 *
 * Created by ch.wang on 17 May 2014.
 */
public class TQuery {

  private final String id;
  private final ImmutableList<String> queries;

  public TQuery(File file) throws IOException {
    id = Files.getNameWithoutExtension(file.getName());
    queries = Files.asCharSource(file, Charsets.US_ASCII).readLines();
  }

  public String getId() {
    return id;
  }

  public ImmutableList<String> getQueries() {
    return queries;
  }
}
