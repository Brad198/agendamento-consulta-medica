import {
  HttpResponseError,
  type ResponseError,
} from "@/utils/errors/ErrorHttpResponse";

export async function cancelAppointmentByPatient(
  appointmentId: string,
  patientId: string,
): Promise<void> {
  const response = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}/appointments/${appointmentId}/patients/${patientId}`,
    {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
    },
  );

  if (!response.ok) {
    const errorData: ResponseError = await response.json();
    throw new HttpResponseError("Failed to cancel appointment", errorData);
  }
}
