dataPoints = 112;

[senderData] = ReadDataFromFile(".\measurements\sender.txt", dataPoints, 2);
[receiverData] = ReadDataFromFile(".\measurements\receiver.txt", dataPoints, 3);

instances = linspace(1, 112, 112);
instances = transpose(instances);

senderHeader = senderData(:,1);
senderTime = senderData(:,2);

receiverHeader = receiverData(:,1);
receiverTime = receiverData(:,2);
timeDifference = receiverData(:,3);

wasPacketReceived = zeros(dataPoints, 1);

failCount = 0;
sum = 0;
for i = 1:dataPoints
    if(senderHeader(i) == receiverHeader(i))
        wasPacketReceived(i) = 1;
        sum = sum + timeDifference(i);
    else
        wasPacketReceived(i) = 0;
        timeDifference(i) = -50;
        failCount = failCount + 1;
    end
end

mean = sum/(dataPoints - failCount);

disp("Mean is: " + mean)

figure

subplot(2, 1, 1) %row, column, position
plot(instances, wasPacketReceived)
grid on
title('Graph to show if the packet was received')
xlabel('Instance')
ylabel('Packet received?')

subplot(2, 1, 2)
plot(instances, timeDifference)
grid on
title('Graph to show the time delays')
xlabel('Instance')
ylabel('Time Delay (ms)')

disp("Time delays when the packets fail are set to an arbitrary value of -50")
disp("These instances are not included in the statistics")

