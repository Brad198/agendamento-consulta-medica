import type { Appointment } from "@/types/entities/Appointment";
import { Visibility } from "@mui/icons-material";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton,
} from "@mui/material";
import { useState } from "react";

type NotesModalModalProps = {
  appointment: Appointment;
};

export function NotesModal({ appointment }: NotesModalModalProps) {
  const [open, setOpen] = useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  return (
    <>
      <IconButton color="inherit" onClick={handleClickOpen}>
        <Visibility />
      </IconButton>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle className="font-bold pb-1">
          Observações do agendamento
        </DialogTitle>
        <DialogContent>
          <DialogContentText className="text-black/60">
            {appointment.notes || "Nenhuma observação para este agendamento."}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button variant="outlined" color="error" onClick={handleClose}>
            Fechar
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
