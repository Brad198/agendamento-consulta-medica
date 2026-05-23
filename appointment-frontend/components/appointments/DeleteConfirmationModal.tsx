import { cancelAppointmentByPatient } from "@/services/cancelAppointmentByPatient";
import type { Appointment } from "@/types/entities/Appointment";
import type { Patient } from "@/types/entities/Patient";
import { HttpResponseError } from "@/utils/errors/ErrorHttpResponse";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { toast } from "sonner";

type DeleteConfirmationModalProps = {
  appointment: Appointment;
  patient: Patient;
};

export function DeleteConfirmationModal({
  appointment,
  patient,
}: DeleteConfirmationModalProps) {
  const [open, setOpen] = useState(false);

  const queryClient = useQueryClient();
  const { mutateAsync, isPending } = useMutation({
    mutationFn: async (appointmentId: string) => {
      await cancelAppointmentByPatient(appointmentId, patient.id);
    },
  });

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleConfirmDelete = async () => {
    await mutateAsync(appointment.id, {
      onSuccess: async () => {
        await queryClient.invalidateQueries({
          queryKey: ["appointments", patient.id],
        });
        toast.success("Agendamento cancelado com sucesso!");
        setOpen(false);
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
    });
  };

  return (
    <>
      <Button color="error" onClick={handleClickOpen} variant="outlined">
        Cancelar
      </Button>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle className="font-bold pb-1">
          Confirmação de Cancelamento
        </DialogTitle>
        <DialogContent>
          <DialogContentText className="text-black/60">
            Tem certeza que deseja cancelar este agendamento com{" "}
            <strong>{appointment.doctor.name}</strong> no dia{" "}
            <strong>
              {new Date(appointment.initialDatetime).toLocaleDateString(
                "pt-BR",
              )}
            </strong>
            ?
          </DialogContentText>
        </DialogContent>
        <DialogActions className="px-3 pb-3 gap-1">
          <Button variant="outlined" onClick={handleClose}>
            Manter Agendamento
          </Button>
          <Button
            color="error"
            loading={isPending}
            onClick={handleConfirmDelete}
          >
            Confirmar Cancelamento
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
