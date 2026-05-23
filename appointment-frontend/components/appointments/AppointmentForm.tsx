"use client";

import {
  createAppointment,
  CreateAppointmentDto,
} from "@/services/createAppointment";
import type { Doctor } from "@/types/entities/Doctor";
import { Patient } from "@/types/entities/Patient";
import { HttpResponseError } from "@/utils/errors/ErrorHttpResponse";
import { createAppointmentSchema } from "@/utils/schemas/appointment";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Avatar,
  Button,
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
  TextField,
} from "@mui/material";
import { DateTimePicker } from "@mui/x-date-pickers";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import dayjs from "dayjs";
import { Controller, useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

type AppointmentFormProps = {
  patient: Patient;
  doctors: Doctor[];
};

export function AppointmentsForm({ patient, doctors }: AppointmentFormProps) {
  const {
    handleSubmit,
    formState: { isSubmitting },
    control,
    watch,
    setValue,
  } = useForm({
    resolver: zodResolver(createAppointmentSchema),
    defaultValues: {
      doctorId: doctors[0].id,
      patientId: patient.id,
      initialDatetime: dayjs().add(1, "day").toISOString(),
      endDatetime: dayjs().add(1, "day").add(1, "hour").toISOString(),
      notes: "",
    },
  });

  const initialDatetimeValue = watch("initialDatetime");

  const queryClient = useQueryClient();

  const { mutateAsync } = useMutation({
    mutationFn: async (appointment: CreateAppointmentDto) => {
      await createAppointment(appointment);
    },
  });

  const handleCreateAppointment = async (
    data: z.output<typeof createAppointmentSchema>,
  ) => {
    await mutateAsync(
      { ...data },
      {
        onSuccess: async () => {
          await queryClient.invalidateQueries({ queryKey: ["appointments"] });
          toast.success("Agendamento criado com sucesso!");
        },
        onError: (error) => {
          let message = "Erro desconhecido.";

          if (error instanceof HttpResponseError) {
            if (typeof error.cause.error === "string") {
              message = error.cause.error;
            }

            if (Array.isArray(error.cause.error)) {
              message = error.cause.error
                .map((err) => Object.values(err).join(": "))
                .join("\n");
            }
          }

          toast.error(message);
        },
      },
    );
  };

  return (
    <form
      onSubmit={handleSubmit(handleCreateAppointment)}
      className="space-y-6"
    >
      <div className="flex items-center gap-3 p-4 bg-blue-50 rounded-xl border border-blue-100">
        <Avatar className="bg-blue-400">{patient.name.charAt(0)}</Avatar>
        <div>
          <p className="text-sm font-semibold text-slate-800">{patient.name}</p>
          <p className="text-xs text-slate-500">Paciente</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Controller
          name="doctorId"
          control={control}
          render={({ field, fieldState: { error } }) => (
            <FormControl
              variant="filled"
              className="md:col-span-2"
              fullWidth
              error={!!error}
            >
              <InputLabel id="doctors-label">Doutor(a)</InputLabel>
              <Select id="doctors-select" labelId="doctors-label" {...field}>
                {doctors.map((doctor) => (
                  <MenuItem key={doctor.id} value={doctor.id}>
                    <div className="flex items-center gap-2">
                      <span className="font-medium">{doctor.name}</span>
                      <span className="text-xs text-slate-500">
                        — {doctor.specialty}
                      </span>
                    </div>
                  </MenuItem>
                ))}
              </Select>
              {error && <FormHelperText>{error.message}</FormHelperText>}
            </FormControl>
          )}
        />

        <div className="md:col-span-2 grid grid-cols-1 sm:grid-cols-2 gap-6">
          <Controller
            name="initialDatetime"
            control={control}
            render={({
              field: { onChange, value, ...fields },
              fieldState: { error },
            }) => (
              <DateTimePicker
                disablePast
                label="Data e hora de início"
                slotProps={{
                  textField: {
                    fullWidth: true,
                    helperText: error?.message,
                    error: !!error,
                  },
                }}
                onChange={(value) => {
                  const date = value?.toISOString() || "";
                  setValue(
                    "endDatetime",
                    dayjs(date).add(1, "hour").toISOString(),
                  );
                  onChange(date);
                }}
                value={value ? dayjs(value) : undefined}
                {...fields}
              />
            )}
          />
          <Controller
            name="endDatetime"
            control={control}
            render={({
              field: { onChange, value, ...fields },
              fieldState: { error },
            }) => (
              <DateTimePicker
                disablePast
                label="Data e hora de término"
                minDateTime={
                  initialDatetimeValue ? dayjs(initialDatetimeValue) : undefined
                }
                slotProps={{
                  textField: {
                    fullWidth: true,
                    helperText: error?.message,
                    error: !!error,
                  },
                }}
                onChange={(value) => {
                  const date = value?.toISOString() || "";
                  onChange(date);
                }}
                value={value ? dayjs(value) : undefined}
                {...fields}
              />
            )}
          />
        </div>

        <Controller
          name="notes"
          control={control}
          render={({ field, fieldState: { error } }) => (
            <TextField
              fullWidth
              className="md:col-span-2"
              label="Observações"
              multiline
              rows={4}
              error={!!error}
              helperText={error?.message}
              placeholder="Adicione informações relevantes sobre a consulta..."
              {...field}
            />
          )}
        />
      </div>

      <div className="pt-2">
        <Button type="submit" fullWidth loading={isSubmitting}>
          Agendar Consulta
        </Button>
      </div>
    </form>
  );
}
