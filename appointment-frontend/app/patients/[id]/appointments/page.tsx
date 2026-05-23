import { Check, ErrorOutlined, KeyboardBackspace } from "@mui/icons-material";
import { Breadcrumbs } from "@mui/material";
import Link from "next/link";
import { AppointmentsForm } from "@/components/appointments/AppointmentForm";
import { AppointmentsTable } from "@/components/appointments/AppointmentTable";
import { getAppointmentsByPatience } from "@/services/getAppointmentsByPatient";
import { getDoctors } from "@/services/getDoctors";
import { getPatientById } from "@/services/getPatientById";
import { HttpResponseError } from "@/utils/errors/ErrorHttpResponse";

type AppointmentsPageProps = {
  params: Promise<{
    id: string;
  }>;
};

export default async function AppointmentsPage({
  params,
}: AppointmentsPageProps) {
  const { id: patientId } = await params;

  const [responsePatient, responseDoctors, responseAppointments] =
    await Promise.allSettled([
      getPatientById(patientId),
      getDoctors(),
      getAppointmentsByPatience(patientId),
    ]);

  if (responsePatient.status === "rejected") {
    const messageError =
      responsePatient.reason instanceof HttpResponseError
        ? responsePatient.reason.cause?.message ||
          "Houve um erro ao carregar os dados do paciente."
        : "Unknown error";

    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] px-4">
        <div className="size-20 bg-red-50 rounded-full flex items-center justify-center mb-6">
          <ErrorOutlined className="text-red-700 text-4xl" />
        </div>
        <h2 className="text-2xl font-bold text-slate-800 mb-2">
          {messageError}
        </h2>
        <p className="text-slate-500 text-center max-w-md">
          Consulte o administrador do sistema para mais informações ou tente
          novamente mais tarde.
        </p>
      </div>
    );
  }

  if (responseDoctors.status === "rejected") {
    const messageError =
      responseDoctors.reason instanceof HttpResponseError
        ? responseDoctors.reason.cause?.message ||
          "Houve um erro ao carregar os dados do paciente."
        : "Unknown error";
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] px-4">
        <div className="size-20 bg-red-50 rounded-full flex items-center justify-center mb-6">
          <ErrorOutlined className="text-red-700 text-4xl" />
        </div>
        <h2 className="text-2xl font-bold text-slate-800 mb-2">
          {messageError}
        </h2>
        <p className="text-slate-500 text-center max-w-md">
          Consulte o administrador do sistema para mais informações ou tente
          novamente mais tarde.
        </p>
      </div>
    );
  }

  const initialAppointments =
    responseAppointments.status === "fulfilled"
      ? responseAppointments.value
      : [];

  return (
    <div className="space-y-8">
      <Breadcrumbs>
        <Link
          href="/patients"
          className="hover:text-blue-600 transition-colors flex items-center gap-1"
        >
          <KeyboardBackspace />
          Pacientes
        </Link>
        <span className="text-slate-800 font-medium">Agendamentos</span>
      </Breadcrumbs>

      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 tracking-tight">
            Agendamento de Consultas
          </h1>
          <p className="mt-1 text-slate-500">
            Gerencie as consultas de {responsePatient.value.name}
          </p>
        </div>
        <div className="inline-flex items-center gap-2 bg-emerald-50 text-emerald-700 px-4 py-2 rounded-full text-sm font-medium">
          <Check />
          {initialAppointments.length}{" "}
          {initialAppointments.length === 1 ? "agendamento" : "agendamentos"}
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm">
        <div className="px-6 py-4 border-b border-slate-100">
          <h2 className="text-lg font-semibold text-slate-800">
            Novo Agendamento
          </h2>
          <p className="text-sm text-slate-500">
            Preencha o formulário abaixo para agendar uma nova consulta
          </p>
        </div>
        <div className="p-6">
          <AppointmentsForm
            patient={responsePatient.value}
            doctors={responseDoctors.value}
          />
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm">
        <div className="px-6 py-4 border-b border-slate-100">
          <h2 className="text-lg font-semibold text-slate-800">
            Histórico de Consultas
          </h2>
          <p className="text-sm text-slate-500">
            Consultas agendadas para {responsePatient.value.name}
          </p>
        </div>
        <div className="p-6">
          <AppointmentsTable
            initialAppointments={initialAppointments}
            patient={responsePatient.value}
          />
        </div>
      </div>
    </div>
  );
}
