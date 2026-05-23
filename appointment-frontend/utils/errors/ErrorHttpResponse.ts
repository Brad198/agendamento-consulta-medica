export type ResponseError = {
  status: number;
  message: string;
  timestamp: string;
  error: string | Record<string, string>[];
};

export class HttpResponseError extends Error {
  readonly cause: ResponseError;

  constructor(message: string, cause: ResponseError) {
    super(message);
    this.name = "ErrorResponse";
    this.cause = cause;
  }
}
