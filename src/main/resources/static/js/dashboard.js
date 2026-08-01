// Dashboard JavaScript Analytics Initialization

document.addEventListener('DOMContentLoaded', function () {
    const deptChartCanvas = document.getElementById('departmentChart');
    if (deptChartCanvas) {
        fetch('/api/analytics/departments')
            .then(response => response.json())
            .then(data => {
                const ctx = deptChartCanvas.getContext('2d');
                new Chart(ctx, {
                    type: 'doughnut',
                    data: {
                        labels: data.labels,
                        datasets: [{
                            data: data.data,
                            backgroundColor: [
                                '#3b82f6',
                                '#8b5cf6',
                                '#10b981',
                                '#f59e0b',
                                '#f43f5e',
                                '#06b6d4'
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
                                    color: '#94a3b8',
                                    font: {
                                        family: 'Plus Jakarta Sans',
                                        size: 12
                                    },
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
