import { z } from "zod";

export const createAppointmentSchema = z
  .object({
    doctorId: z.uuidv7(),
    patientId: z.uuidv7(),
    initialDatetime: z.iso.datetime(),
    endDatetime: z.iso.datetime(),
    notes: z.string().nullable(),
  })
  .refine(
    (data) => {
      const start = new Date(data.initialDatetime);
      const end = new Date(data.endDatetime);
      return end >= start;
    },
    {
      message: "A data final não pode ser menor que a data inicial",
      path: ["endDatetime"],
    },
  );
