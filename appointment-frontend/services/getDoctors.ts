import type { Doctor } from "@/types/entities/Doctor";
import {
  HttpResponseError,
  type ResponseError,
} from "@/utils/errors/ErrorHttpResponse";

export async function getDoctors(): Promise<Doctor[]> {
  const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/doctors`);
  const data = await response.json();
  if (!response.ok) {
    throw new HttpResponseError(
      "Failed to fetch doctors",
      data as ResponseError,
    );
  }
  return data;
}
