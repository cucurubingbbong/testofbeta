const roadmapDiv = document.getElementById('roadmap');
const postsDiv = document.getElementById('posts');

document.getElementById('generate').addEventListener('click', async () => {
    const payload = {
        jdText: document.getElementById('jdText').value || 'Spring 백엔드 개발자를 찾습니다. Java, Spring, MySQL',
        durationMonths: Number(document.getElementById('duration').value),
        dailyHours: Number(document.getElementById('hours').value),
        level: document.getElementById('level').value
    };

    const res = await fetch('http://localhost:8080/api/roadmap/from-jd', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    const data = await res.json();
    renderRoadmap(data);
});

document.getElementById('fetchPosts').addEventListener('click', async () => {
    const res = await fetch('http://localhost:8080/api/crawl/daily');
    const data = await res.json();
    postsDiv.innerHTML = data.map(post => `
        <div class="card">
            <h3>${post.role}</h3>
            <p>${post.company} • ${post.location}</p>
            <p class="badge">마감: ${post.deadline}</p>
            <div>${post.keywords.map(k => `<span class="badge">${k}</span>`).join('')}</div>
            <a href="${post.url}" target="_blank">공고 보기</a>
        </div>
    `).join('');
});

function renderRoadmap(data) {
    roadmapDiv.innerHTML = `
        <div class="card">
            <strong>${data.targetRole}</strong><br>
            총 ${data.totalWeeks}주 • 하루 ${data.dailyHours}시간 권장
        </div>
        ${data.steps.map(step => `
            <div class="card">
                <h3>${step.title} (${step.estimatedWeeks}주)</h3>
                ${step.weeks.map(week => `
                    <div class="card">
                        <strong>${week.weekNumber}주차</strong>
                        <div>${week.topics.map(t => `<span class="badge">${t}</span>`).join('')}</div>
                        <p>${week.mission}</p>
                    </div>
                `).join('')}
            </div>
        `).join('')}
    `;
}
