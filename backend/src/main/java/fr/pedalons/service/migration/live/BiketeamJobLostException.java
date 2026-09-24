package fr.pedalons.service.migration.live;

/**
 * This worker no longer owns the job: it was declared stuck and requeued, or its attempt counter
 * moved. The run stops without touching the row.
 */
public class BiketeamJobLostException extends RuntimeException {

  public BiketeamJobLostException(String message) {
    super(message);
  }
}
