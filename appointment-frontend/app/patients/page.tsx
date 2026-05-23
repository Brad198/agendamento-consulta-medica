import GroupIcon from "@mui/icons-material/Group";
import PersonIcon from "@mui/icons-material/Person";
import { Chip } from "@mui/material";
import { PatientsSelector } from "@/components/patients/PatientsSelector";
import { getPatients } from "@/services/getPatients";

export default async function AppointmentsPage() {
  const patients = await getPatients();

  if (patients.length === 0) {
    return (
      <div className="py-12 max-w-sm m-auto">
        <div className="flex flex-col items-center justify-center gap-4">
          <div className="size-16 rounded-full flex justify-center items-center bg-gray-100">
            <GroupIcon className="text-4xl text-gray-400" />
          </div>

          <div>
            <h5 className="text-2xl font-bold mb-2">
              Nenhum paciente encontrado
            </h5>
            <p className="text-gray-600">
              Por favor, adicione pacientes para agendar consultas.
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="py-4 px-2 sm:px-3">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 mb-6">
        <div>
          <h1 className="text-3xl font-bold mb-1 text-center sm:text-left">
            Pacientes
          </h1>
          <p className="text-gray-600 text-center sm:text-left">
            Selecione um paciente para visualizar seus agendamentos
          </p>
        </div>

        <Chip
          icon={<PersonIcon />}
          label={`${patients.length} ${patients.length === 1 ? "paciente" : "pacientes"}`}
          color="primary"
          variant="filled"
        />
      </div>

      <PatientsSelector patients={patients} />
    </div>
  );
}
