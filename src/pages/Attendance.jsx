import { useEffect, useState } from "react";
import {
  ATTENDANCE_API,
  apiGet,
  apiPost
} from "../api/api";

export default function Attendance() {

  const [attendance, setAttendance] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  const employeeId = localStorage.getItem("employeeId");

  const roles = JSON.parse(
    localStorage.getItem("roles") || "[]"
  );

  const isAdmin =
    roles.includes("ROLE_HR_ADMIN") ||
    roles.includes("ROLE_SUPER_ADMIN");

  useEffect(() => {
    loadAttendance();
  }, []);

  async function loadAttendance() {

    try {

      let response;

      if (isAdmin) {

        response = await apiGet(
          ATTENDANCE_API
        );

      } else {

        response = await apiGet(
          `${ATTENDANCE_API}/employee/${employeeId}`
        );

      }

      setAttendance(response);

    } catch (err) {

      console.error(err);

    } finally {

      setLoading(false);

    }

  }

  async function handleCheckIn() {

    try {

      await apiPost(
        `${ATTENDANCE_API}/check-in`,
        {
          employeeId
        }
      );

      alert("Check In Successful");

      loadAttendance();

    } catch (err) {

      alert(err.message);

    }

  }

  async function handleCheckOut() {

    try {

      await apiPost(
        `${ATTENDANCE_API}/check-out/${employeeId}`,
        {}
      );

      alert("Check Out Successful");

      loadAttendance();

    } catch (err) {

      alert(err.message);

    }

  }

  const filtered = attendance.filter((a) =>

    a.employeeId
      .toLowerCase()
      .includes(search.toLowerCase())

  );

  if (loading) {

    return <h2>Loading Attendance...</h2>;

  }

  return (

    <div className="page">

      <h1>Attendance Records</h1>

      <div
        style={{
          display: "flex",
          gap: "10px",
          marginBottom: "20px"
        }}
      >

        <button onClick={handleCheckIn}>
          Check In
        </button>

        <button onClick={handleCheckOut}>
          Check Out
        </button>

      </div>

      {isAdmin && (

        <input
          type="text"
          placeholder="Search Employee ID..."
          value={search}
          onChange={(e) =>
            setSearch(e.target.value)
          }
          className="search-box"
        />

      )}

      <table className="data-table">

        <thead>

          <tr>

            <th>Employee</th>

            <th>Date</th>

            <th>Status</th>

            <th>Check In</th>

            <th>Check Out</th>

            <th>Working Hours</th>

          </tr>

        </thead>

        <tbody>

          {(isAdmin ? filtered : attendance).map((a) => (

            <tr key={a.id}>

              <td>{a.employeeId}</td>

              <td>{a.attendanceDate}</td>

              <td>

                <span
                  className={
                    a.status === "PRESENT"
                      ? "status-present"
                      : "status-absent"
                  }
                >
                  {a.status}
                </span>

              </td>

              <td>

                {a.checkInTime
                  ? new Date(
                      a.checkInTime
                    ).toLocaleTimeString()
                  : "-"}

              </td>

              <td>

                {a.checkOutTime
                  ? new Date(
                      a.checkOutTime
                    ).toLocaleTimeString()
                  : "-"}

              </td>

              <td>

                {a.workingHours != null
                  ? `${a.workingHours.toFixed(
                      2
                    )} hrs`
                  : "-"}

              </td>

            </tr>

          ))}

        </tbody>

      </table>

    </div>

  );

}