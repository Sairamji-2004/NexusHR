import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { NOTIFICATION_API } from "../api/api";
import { useNavigate } from "react-router-dom";


export default function NotificationBell() {

    const { employeeId } = useAuth();
    const navigate = useNavigate();

    const [notifications, setNotifications] = useState([]);
    const [open, setOpen] = useState(false);


    const loadNotifications = async () => {
        try {

            const token = localStorage.getItem("token");

            const response = await fetch(
                `${NOTIFICATION_API}/employee/${employeeId}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );

            const data = await response.json();

          setNotifications(
    data.sort(
        (a, b) =>
            new Date(b.createdAt) -
            new Date(a.createdAt)
    )
);

        } catch(error) {
            console.log(error);
        }
    };


  useEffect(() => {

    if (!employeeId) return;

    loadNotifications();

    const interval = setInterval(() => {
        loadNotifications();
    }, 5000);

    return () => clearInterval(interval);

}, [employeeId]);

    const unreadCount = notifications.filter(
        n => !n.isRead
    ).length;


    return (

        <div style={{position:"relative"}}>

            <button
                onClick={() => setOpen(!open)}
                style={{
                    background:"none",
                    border:"none",
                    fontSize:"22px",
                    cursor:"pointer"
                }}
            >

                🔔

                {unreadCount > 0 && (
                    <span
                        style={{
                            background:"red",
                            color:"white",
                            borderRadius:"50%",
                            padding:"3px 7px",
                            fontSize:"12px",
                            position:"absolute",
                            top:"-5px",
                            right:"-5px"
                        }}
                    >
                        {unreadCount}
                    </span>
                )}

            </button>



            {
            open && (
<div
style={{
    position:"absolute",
    right:0,
    top:"45px",
    width:"350px",
    background:"#ffffff",
    color:"#111",
    borderRadius:"10px",
    boxShadow:"0 8px 25px rgba(0,0,0,0.25)",
    zIndex:9999,
    overflow:"hidden"
}}
>

                    <h4
                    style={{
                        padding:"12px",
                        margin:0,
                        borderBottom:"1px solid #ddd"
                    }}
                    >
                        Notifications
                    </h4>



                    {
                    notifications
                    .slice(0,5)
                    .map(notification => (

                        <div
                        key={notification.id}
                       style={{
    padding: "12px",
    borderBottom: "1px solid #eee",
    background: notification.isRead
        ? "#fff"
        : "#eef7ff"
}}
                        >

                            <strong>
                                {notification.title}
                            </strong>

                      <p style={{ margin: "5px 0" }}>
    {notification.message}
</p>

<small style={{ color: "#777" }}>
    {new Date(notification.createdAt)
        .toLocaleString()}
</small>
                        </div>

                    ))
                    }



                    <div
                    style={{
                        textAlign:"center",
                        padding:"12px",
                        cursor:"pointer"
                    }}

                  onClick={() => {

    setOpen(false);

    window.location.href = "/notifications";

}}
                    >

                        View All

                    </div>


                </div>

            )
            }


        </div>

    );

}