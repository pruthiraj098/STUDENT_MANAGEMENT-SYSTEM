// Student Management System - Dashboard JavaScript Initialization

document.addEventListener('DOMContentLoaded', function () {
    // Department Analytics Doughnut Chart
    const deptChartCanvas = document.getElementById('departmentChart');
    if (deptChartCanvas) {
        fetch('/api/analytics/departments')
            .then(response => response.json())
            .then(data => {
                const ctxDept = deptChartCanvas.getContext('2d');
                new Chart(ctxDept, {
                    type: 'doughnut',
                    data: {
                        labels: data.labels,
                        datasets: [{
                            data: data.data,
                            backgroundColor: [
                                '#042954',
                                '#feb600',
                                '#39b54a',
                                '#007bff',
                                '#e91e63',
                                '#00bcd4'
                            ],
                            borderWidth: 0,
                            hoverOffset: 6
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'bottom',
                                labels: {
                                    color: '#555555',
                                    font: { family: 'Roboto', size: 12 },
                                    padding: 15
                                }
                            }
                        },
                        cutout: '70%'
                    }
                });
            })
            .catch(err => console.error('Error loading analytics chart:', err));
    }
});
