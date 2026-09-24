package fr.pedalons.service.migration.live;

/**
 * A business failure of a job — conflict, lost rights — which no retry would fix: the job goes
 * straight to FAILED with this code.
 */
public class BiketeamJobFailure extends RuntimeException {

  private final String code;

  public BiketeamJobFailure(String code, String message) {
    super(message);
    this.code = code;
  }

  public String code() {
    return code;
  }
}
