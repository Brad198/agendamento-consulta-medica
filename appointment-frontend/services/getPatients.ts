import type { Patient } from "@/types/entities/Patient";
import {
  HttpResponseError,
  type ResponseError,
} from "@/utils/errors/ErrorHttpResponse";

export async function getPatients(): Promise<Patient[]> {
  const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/patients`);
  const data = await response.json();
  if (!response.ok) {
    throw new HttpResponseError(
      "Failed to fetch patients",
      data as ResponseError,
    );
  }
  return data;
}
