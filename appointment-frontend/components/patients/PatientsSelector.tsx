"use client";

import BadgeIcon from "@mui/icons-material/Badge";
import CalendarTodayIcon from "@mui/icons-material/CalendarToday";
import PhoneIcon from "@mui/icons-material/Phone";
import { Avatar, Card, CardActionArea, CardContent, Chip } from "@mui/material";
import { useRouter } from "next/navigation";
import type { Patient } from "@/types/entities/Patient";

type PatientsSelectorProps = {
  patients: Patient[];
};

function formatDate(dateStr: string) {
  const [year, month, day] = dateStr.split("-");
  return `${day}/${month}/${year}`;
}

export function PatientsSelector({ patients }: PatientsSelectorProps) {
  const router = useRouter();

  const handleSelectPatient = (patientId: string) => {
    router.push(`/patients/${patientId}/appointments`);
  };

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
      {patients.map((patient) => (
        <Card key={patient.id} variant="outlined">
          <CardActionArea
            onClick={() => handleSelectPatient(patient.id)}
            className="p-0"
          >
            <CardContent className="p-4">
              <div className="flex items-center gap-4 mb-4">
                <Avatar className="bg-blue-400">
                  {patient.name.charAt(0)}
                </Avatar>

                <div>
                  <h3 className="text-lg font-bold">{patient.name}</h3>
                  <Chip label="Paciente" size="small" variant="outlined" />
                </div>
              </div>

              <div className="flex flex-col gap-2">
                <div className="flex items-center gap-1.5">
                  <CalendarTodayIcon className="text-gray-500 text-lg" />
                  <p className="text-gray-500">
                    {formatDate(patient.birthDate)}
                  </p>
                </div>

                <div className="flex items-center gap-1.5">
                  <PhoneIcon className="text-gray-500 text-lg" />
                  <p className="text-gray-500">{patient.phone}</p>
                </div>

                <div className="flex items-center gap-1.5">
                  <BadgeIcon className="text-gray-500 text-lg" />
                  <p className="text-gray-500">{patient.documentId}</p>
                </div>
              </div>
            </CardContent>
          </CardActionArea>
        </Card>
      ))}
    </div>
  );
}
