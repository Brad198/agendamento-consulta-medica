"use client";

import { ErrorOutlined, Person } from "@mui/icons-material";
import {
  Avatar,
  Chip,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { getAppointmentsByPatience } from "@/services/getAppointmentsByPatient";
import type { Appointment } from "@/types/entities/Appointment";
import type { Patient } from "@/types/entities/Patient";
import { DeleteConfirmationModal } from "./DeleteConfirmationModal";
import { NotesModal } from "./NotesModal";

type AppointmentsTableProps = {
  initialAppointments: Appointment[];
  patient: Patient;
};

function formatDatetime(datetime: string) {
  return new Date(datetime).toLocaleString("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export function AppointmentsTable({
  initialAppointments,
  patient,
}: AppointmentsTableProps) {
  const { data: appointments } = useQuery({
    initialData: initialAppointments,
    queryKey: ["appointments", patient.id],
    queryFn: () => getAppointmentsByPatience(patient.id),
  });

  if (appointments.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-16">
        <div className="size-12 bg-red-50 rounded-full flex items-center justify-center mb-4">
          <ErrorOutlined className="text-red-700" />
        </div>
        <h3 className="text-lg font-semibold text-slate-700 mb-1">
          Nenhuma consulta agendada
        </h3>
        <p className="text-sm text-slate-500 text-center max-w-sm">
          {patient.name} ainda não possui consultas. Use o formulário acima para
          agendar a primeira consulta.
        </p>
      </div>
    );
  }

  return (
    <TableContainer component={Paper}>
      <Table size="small">
        <TableHead>
          <TableRow className="bg-gray-100 [&>th]:text-gray-900 [&>th]:font-medium">
            <TableCell>Médico(a)</TableCell>
            <TableCell align="center">Especialidade</TableCell>
            <TableCell align="center">Data de início</TableCell>
            <TableCell align="center">Data de término</TableCell>
            <TableCell align="center">Observações</TableCell>
            <TableCell align="center">Ação</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {appointments.map((appointment) => (
            <TableRow
              key={appointment.id}
              className="hover:bg-gray-50 transition-colors"
            >
              <TableCell>
                <div className="flex items-center gap-3">
                  <Avatar className="bg-green-600">
                    <Person />
                  </Avatar>
                  <span className="font-medium text-slate-800">
                    {appointment.doctor.name}
                  </span>
                </div>
              </TableCell>
              <TableCell align="center">
                <Chip
                  label={appointment.doctor.specialty}
                  size="small"
                  color="info"
                  variant="outlined"
                />
              </TableCell>
              <TableCell align="center">
                {formatDatetime(appointment.initialDatetime)}
              </TableCell>
              <TableCell align="center">
                {formatDatetime(appointment.endDatetime)}
              </TableCell>
              <TableCell align="center">
                <NotesModal appointment={appointment} />
              </TableCell>
              <TableCell align="center">
                <DeleteConfirmationModal
                  appointment={appointment}
                  patient={patient}
                />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
